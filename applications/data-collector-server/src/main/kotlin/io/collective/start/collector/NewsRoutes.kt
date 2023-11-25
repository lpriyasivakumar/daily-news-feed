package io.collective.start.collector

import io.collective.news.NewsService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

fun Route.newsRouting(newsService: NewsService) {
    route("/news") {
        get {
            val params = call.parameters
            val filterby = params["filterby"]
            call.respond(newsService.findAllBySentiment(filterby))
        }
    }
}

fun Application.registerNewsRoutes(newsService: NewsService) {
    routing {
        newsRouting(newsService)
    }
}
