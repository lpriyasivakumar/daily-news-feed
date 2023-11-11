package io.collective.start.analyzer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import io.collective.news.NewsArticle
import org.slf4j.LoggerFactory

class AnalysisService(private val rabbitUri: String, private val routingKey: String) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val factory = ConnectionFactory().apply { useNio() }

    fun analyzeAndSend(article: NewsArticle) {
        logger.info("Start Analysis")
        //Running sentiment analysis
        logger.info("Dispatching article with title{} to save queue", article.title)
        val body = mapper.writeValueAsString(article).toByteArray()
        try {
            factory.setUri(rabbitUri)
            factory.setConnectionTimeout(30000)
            factory.newConnection().use { connection ->
                connection.createChannel().use { channel ->
                    channel.basicPublish("news-save-exchange", routingKey, MessageProperties.PERSISTENT_BASIC, body)
                }
            }
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