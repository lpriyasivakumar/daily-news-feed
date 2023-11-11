package io.collective.start.analyzer

import io.collective.messaging.BasicRabbitConfiguration
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
    BasicRabbitConfiguration(rabbitUri, exchange = "news-save-exchange", queue = "news-save", routingKey = "auto-save", subscribe = false, null).setUp()
    //Save exchange - to consume from
    BasicRabbitConfiguration(rabbitUri, exchange = "news-analysis-exchange", queue = "news-analysis", routingKey = "auto-analysis", subscribe = true, callback = AnalysisTaskHandler(rabbitUri)).setUp()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8081
    embeddedServer(Netty, port, watchPaths = listOf("data-analyzer-server"), module = Application::module).start()
}