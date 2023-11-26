package io.collective.start.collector

import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import io.collective.news.NewsArticle
import io.collective.news.NewsResponse
import io.collective.workflow.Worker
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class CollectionWorker(override val name: String = "data-collector", val rabbitUri: String, val routingKey: String, registry: MetricRegistry) : Worker<CollectionTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val factory = ConnectionFactory().apply { useNio() }
    private val client = HttpClient {
        expectSuccess = true
        install(JsonFeature) {
            serializer = JacksonSerializer {
            }
        }
    }
    private val articleDispatches = registry.meter("article-collections")

    override fun execute(task: CollectionTask) {
        runBlocking {
            logger.info("starting data collection.")
            val response: NewsResponse = client.get(task.url) {
                accept(ContentType.Application.Json)
                parameter("qInTitle", "Technology OR IT OR AI")
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
            factory.setUri(rabbitUri)
            factory.setConnectionTimeout(30000)
            factory.newConnection().use { connection ->
                connection.createChannel().use { channel ->
                    channel.basicPublish("news-analysis-exchange", routingKey, MessageProperties.PERSISTENT_BASIC, body)
                }
            }
            articleDispatches.mark()
        } catch (e: Exception) {
            logger.error(
                "Error, failed to queue article with id {} and title {}",
                article.sourceId,
                article.title,
            )
            e.printStackTrace()
        }
    }
}