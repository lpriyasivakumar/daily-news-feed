package io.collective.start.analyzer

import io.collective.messaging.BasicRabbitConfiguration
import io.collective.messaging.BasicRabbitListener
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.util.*

fun Application.module() {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val rabbitUri = System.getenv("RABBIT_URI") ?: "amqp://localhost:5672"
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("hi!", ContentType.Text.Html)
        }
    }
    //Analysis exchange - to publish to
    BasicRabbitConfiguration(rabbitUri, exchange = "news-save-exchange", queue = "news-save", routingKey = "auto-save").setUp()
    //Save exchange - to consume from
    BasicRabbitConfiguration(rabbitUri, exchange = "news-analysis-exchange", queue = "news-analysis", routingKey = "auto-analysis").setUp()
    BasicRabbitListener(
        rabbitUri = rabbitUri,
        queue = "news-analysis",
        delivery = AnalysisTaskHandler(rabbitUri),
        cancel = { logger.info("Cancelled") },
        autoAck = false,
    ).start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8081
    embeddedServer(Netty, port, watchPaths = listOf("data-analyzer-server"), module = Application::module).start()
}