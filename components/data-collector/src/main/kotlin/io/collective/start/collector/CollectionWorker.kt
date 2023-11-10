package io.collective.start.collector

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.collective.messaging.BasicDispatcher
import io.collective.news.NewsArticle
import io.collective.news.NewsResponse
import io.collective.workflow.Worker
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class CollectionWorker(override val name: String = "data-collector", val rabbitUri: String, val routingKey: String) : Worker<CollectionTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val client = HttpClient {
        expectSuccess = true
        install(JsonFeature) {
            serializer = JacksonSerializer {
            }
        }
    }

    override fun execute(task: CollectionTask) {
        runBlocking {
            logger.info("starting data collection.")
            val response: NewsResponse = client.get(task.url) {
                accept(ContentType.Application.Json)
                parameter("qInTitle", "autism OR developmental disability")
                parameter("apiKey", task.apiKey)
                parameter("language", "en")

            }
            response.results.forEach { it -> send(it) }
            client.close()

            logger.info("completed data collection.")
        }
    }

    private fun send(article: NewsArticle) {
        try {
            val body = mapper.writeValueAsString(article).toByteArray()
            BasicDispatcher(rabbitUri,"news-analysis-exchange",body,routingKey).send()
        } catch (e: Exception) {
            logger.error(
                "Error, failed to queue article with id {} and title {}",
                article.id,
                article.title,
            )
            e.printStackTrace()
        }
    }
}