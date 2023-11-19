package io.collective.metrics

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.exporter.common.TextFormat.write004
import kotlinx.coroutines.cancel
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.title
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun Route.metricsRouting(registry: CollectorRegistry) {
    route("/metrics"){
        get {
            call.respond(registry.metricFamilySamples().toList())
            }
        }
    }

fun Application.registerMetricsRoute(registry: CollectorRegistry) {
    routing {
        metricsRouting(registry)
    }
}