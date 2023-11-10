package io.collective.start.collector

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.collective.database.dataSource
import io.collective.messaging.BasicRabbitConfiguration
import io.collective.messaging.BasicRabbitListener
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
import org.slf4j.LoggerFactory.*
import java.util.*

fun Application.module() {
    val logger =  getLogger(this.javaClass)
    val newsService = NewsService(NewsDataGateway(dataSource()))
    val rabbitUri = System.getenv("RABBIT_URI") ?: "amqp://localhost:5672"
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
    //Analysis exchange - to publish to
    BasicRabbitConfiguration(rabbitUri, exchange = "news-analysis-exchange", queue = "news-analysis", routingKey = "auto-analysis").setUp()
    //Save exchange - to consume from
    BasicRabbitConfiguration(rabbitUri, exchange = "news-save-exchange", queue = "news-save", routingKey = "auto-save").setUp()
    BasicRabbitListener(
        rabbitUri = rabbitUri,
        queue = "news-save",
        delivery = SaveHandler(newsService),
        cancel = { logger.info("Cancelled") },
        autoAck = false,
    ).start()
    val scheduler = WorkScheduler(CollectionWorkFinder(), mutableListOf(CollectionWorker(rabbitUri= rabbitUri, routingKey="auto-analysis")), 30)
    scheduler.start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8761
    embeddedServer(Netty, port, watchPaths = listOf("data-collector-server"), module = Application::module).start()
}