package io.collective.start.collector

import io.collective.news.NewsService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.newsRouting(newsService: NewsService) {
    route("/news") {
        get {
            call.respond(newsService.findAll())
        }
    }
}

fun Application.registerNewsRoutes(newsService: NewsService) {
    routing {
        newsRouting(newsService)
    }
}
