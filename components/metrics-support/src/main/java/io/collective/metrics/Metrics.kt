package io.collective.metrics

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.prometheus.client.CollectorRegistry

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