package io.collective.start

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.cache.ClassTemplateLoader
import io.collective.metrics.registerHealthRoute
import io.collective.metrics.registerMetricsRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.dropwizard.DropwizardExports
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

fun Application.module(registry: MetricRegistry, collectorRegistry: CollectorRegistry) {
    val reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger("io.collective.start"))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build()
    collectorRegistry.register(DropwizardExports(registry))
    reporter.start(5, TimeUnit.SECONDS)
    val pageRequests = registry.meter("news-feed-requests")
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
    install(Routing) {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("headers" to headers())))
            pageRequests.mark()
        }
        post("/echo") {
            val params = call.receiveParameters()
            val text = params.getOrFail("user_input")
            call.respond(FreeMarkerContent("echo.ftl", mapOf("headers" to headers(), "text" to text)))
        }
        static("images") { resources("images") }
        static("style") { resources("style") }
    }
    registerHealthRoute()
    registerMetricsRoute(collectorRegistry)
}

private fun PipelineContext<Unit, ApplicationCall>.headers(): MutableMap<String, String> {
    val headers = mutableMapOf<String, String>()
    call.request.headers.entries().forEach { entry ->
        headers[entry.key] = entry.value.joinToString()
    }
    return headers
}

fun main() {
    val registry = MetricRegistry()
    val collectorRegistry = CollectorRegistry.defaultRegistry
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8180
    embeddedServer(Netty, port, watchPaths = listOf("basic-server"), module = { module(registry, collectorRegistry) }).start()
}
