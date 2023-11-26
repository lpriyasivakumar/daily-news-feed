package io.collective.start

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.cache.ClassTemplateLoader
import io.collective.metrics.registerHealthRoute
import io.collective.metrics.registerMetricsRoute
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.dropwizard.DropwizardExports
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

fun Application.module(registry: MetricRegistry, collectorRegistry: CollectorRegistry, client: HttpClient) {
    val reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger("io.collective.start"))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build()
    collectorRegistry.register(DropwizardExports(registry))
    reporter.start(5, TimeUnit.SECONDS)
    val pageRequests = registry.meter("news-feed-requests")
    val newsApiUrl = System.getenv("API_URL") ?: "http://localhost:8761/news"
    install(DefaultHeaders)
    install(CallLogging)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter())
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            writerWithDefaultPrettyPrinter()
        }
    }
    registerNewsFeeds(newsApiUrl, pageRequests, client.engine)
    registerHealthRoute()
    registerMetricsRoute(collectorRegistry)
}

fun main() {
    val registry = MetricRegistry()
    val collectorRegistry = CollectorRegistry.defaultRegistry
    val client = HttpClient { install(JsonFeature) { serializer = JacksonSerializer() } }
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8180
    embeddedServer(Netty, port, watchPaths = listOf("basic-server"), module = { module(registry, collectorRegistry, client) }).start()
}
