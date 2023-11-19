package io.collective.start.collector

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.collective.database.dataSource
import io.collective.messaging.BasicRabbitConfiguration
import io.collective.metrics.registerHealthRoute
import io.collective.metrics.registerMetricsRoute
import io.collective.news.NewsDataGateway
import io.collective.news.NewsService
import io.collective.workflow.WorkScheduler
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.dropwizard.DropwizardExports
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.*
import java.util.*
import java.util.concurrent.TimeUnit

fun Application.module(registry: MetricRegistry, collectorRegistry: CollectorRegistry) {
    val logger =  getLogger(this.javaClass)
    val newsService = NewsService(NewsDataGateway(dataSource()))
    val rabbitUri = System.getenv("RABBIT_URI") ?: "amqp://localhost:5672"
    val reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger("io.collective.start"))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build()
    collectorRegistry.register(DropwizardExports(registry))
    reporter.start(5, TimeUnit.SECONDS)
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter())
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            writerWithDefaultPrettyPrinter()
        }
    }
    install(Routing) {
        get("/") {
            call.respondText("hi!", ContentType.Text.Html)
        }
    }
    registerNewsRoutes(newsService)
    registerHealthRoute()
    registerMetricsRoute(collectorRegistry)
    //Analysis exchange - to publish to
    BasicRabbitConfiguration(rabbitUri, exchange = "news-analysis-exchange", queue = "news-analysis", routingKey = "auto-analysis", subscribe = false, null).setUp()
    //Save exchange - to consume from
    BasicRabbitConfiguration(rabbitUri, exchange = "news-save-exchange", queue = "news-save", routingKey = "auto-save", subscribe = true, SaveHandler(newsService, registry)).setUp()
    val scheduler = WorkScheduler(CollectionWorkFinder(), mutableListOf(CollectionWorker(rabbitUri= rabbitUri, routingKey="auto-analysis", registry = registry)), 30)
    scheduler.start()
}

fun main() {
    val registry = MetricRegistry()
    val collectorRegistry = CollectorRegistry.defaultRegistry
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8761
    embeddedServer(Netty, port, watchPaths = listOf("data-collector-server"), module = { module(registry, collectorRegistry) }).start()
}