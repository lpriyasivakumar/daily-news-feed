package io.collective.start.collector

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.collective.workflow.WorkScheduler
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

fun Application.module() {
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
    registerNewsRoutes()
    val scheduler = WorkScheduler<CollectionTask>(CollectionWorkFinder(), mutableListOf(CollectionWorker()), 30)
    scheduler.start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8761
    embeddedServer(Netty, port, watchPaths = listOf("data-collector-server"), module = Application::module).start()
}