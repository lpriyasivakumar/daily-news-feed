package io.collective.metrics

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.routing.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title

fun Route.healthRouting() {
    route("/health-check"){
        get {
            call.respondHtml (HttpStatusCode.OK) {
                head {
                    title {
                        +"Health check"
                    }
                }
                body {
                    h1 {
                        +"Status Up!"
                    }
                }
            }
        }
    }
}

fun Application.registerHealthRoute() {
    routing {
         healthRouting()
    }
}
