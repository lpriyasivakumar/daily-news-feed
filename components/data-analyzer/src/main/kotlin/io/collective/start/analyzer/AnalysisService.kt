package io.collective.start.analyzer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.collective.messaging.BasicDispatcher
import io.collective.news.NewsArticle
import org.slf4j.LoggerFactory

class AnalysisService(private val rabbitUri: String, private val routingKey: String) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    fun analyzeAndSend(article: NewsArticle) {
        logger.info("Start Analysis")
        //Running sentiment analysis
        logger.info("Dispatching article with title{} to save queue", article.title)
        val body = mapper.writeValueAsString(article).toByteArray()
        try {
             BasicDispatcher(rabbitUri, "news-save-exchange",body, routingKey)
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