package io.collective.start.collector

import io.collective.start.rest.ApiInterface
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.newsRouting(apiInterface: ApiInterface) {
    route("/news") {
        get {
            call.respond(apiInterface.findAll())
        }
    }
}

fun Application.registerNewsRoutes(apiInterface: ApiInterface) {
    routing {
        newsRouting(apiInterface)
    }
}
