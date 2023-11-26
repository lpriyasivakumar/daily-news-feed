package io.collective.start

import com.codahale.metrics.Meter
import io.collective.news.NewsArticle
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory

fun Route.newsFeedRouting(newsApiUrl: String, pageRequests: Meter, engine: HttpClientEngine) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    route("/news-feed") {
        get {
            logger.info("Received news-feed request")
            val filterby = call.parameters["filterby"] ?: "all"
            val client = HttpClient(engine) { install(JsonFeature) { serializer = JacksonSerializer() } }
            val response: List<NewsArticle> = client.get(newsApiUrl){
                parameter("filterby", filterby)
            }
            client.close()
            call.respond(FreeMarkerContent("news.ftl", mapOf("headers" to headers(), "articles" to response)))
            pageRequests.mark()
        }
    }
    route("/") {
        get {
            call.respond(FreeMarkerContent("index.ftl", mapOf("headers" to headers())))
        }
    }
    route("/echo") {
        post {
            val params = call.receiveParameters()
            val text = params.getOrFail("user_input")
            call.respond(FreeMarkerContent("echo.ftl", mapOf("headers" to headers(), "text" to text)))
        }
    }
    static("images") { resources("images") }
    static("style") { resources("style") }
}

fun Application.registerNewsFeeds(newsApiUrl: String, pageRequests: Meter, engine: HttpClientEngine) {
    routing {
        newsFeedRouting(newsApiUrl, pageRequests, engine)
    }
}

private fun PipelineContext<Unit, ApplicationCall>.headers(): MutableMap<String, String> {
    val headers = mutableMapOf<String, String>()
    call.request.headers.entries().forEach { entry ->
        headers[entry.key] = entry.value.joinToString()
    }
    return headers
}
