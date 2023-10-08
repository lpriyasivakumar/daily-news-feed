package io.collective.start.collector

import io.collective.database.dataSource
import io.collective.news.NewsDataGateway
import io.collective.news.NewsResponse
import io.collective.news.NewsService
import io.collective.start.rest.ApiInterface
import io.collective.workflow.Worker
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class CollectionWorker(override val name: String = "data-collector") : Worker<CollectionTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val dataGateway = NewsDataGateway(dataSource())
    private val newsService = NewsService(dataGateway)
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
            save(response)
            client.close()

            logger.info("completed data collection.")
        }
    }

    private fun save(response: NewsResponse) {
        response.results.forEach { it -> newsService.save(it) }
    }
}