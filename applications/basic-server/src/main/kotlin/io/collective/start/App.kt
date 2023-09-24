package io.collective.start

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.util.*

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(Routing) {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("headers" to headers())))
        }
        post("/echo") {
            val params = call.receiveParameters()
            val text = params.getOrFail("user_input")
            call.respond(FreeMarkerContent("echo.ftl", mapOf("headers" to headers(), "text" to text)))
        }
        static("images") { resources("images") }
        static("style") { resources("style") }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.headers(): MutableMap<String, String> {
    val headers = mutableMapOf<String, String>()
    call.request.headers.entries().forEach { entry ->
        headers[entry.key] = entry.value.joinToString()
    }
    return headers
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("WEB_APP_PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port, watchPaths = listOf("basic-server"), module = { module() }).start()
}
