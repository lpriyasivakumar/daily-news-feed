package io.collective.metrics

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import java.io.StringWriter

fun Route.metricsRouting(registry: CollectorRegistry) {
    route("/metrics"){
        get {
            val writer = StringWriter()
            TextFormat.write004(writer, registry.metricFamilySamples())
            writer.flush()
            call.respondText(writer.toString())
            }
        }
    }

fun Application.registerMetricsRoute(registry: CollectorRegistry) {
    routing {
        metricsRouting(registry)
    }
}