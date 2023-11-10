package io.collective.start.analyzer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import io.collective.news.NewsArticle
import org.slf4j.LoggerFactory

class AnalysisTaskHandler(rabbitUri: String) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper().registerKotlinModule()
    private var channel: Channel? = null
    private val analysisService = AnalysisService(rabbitUri, "auto-save")
    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String?, message: Delivery) {
        val article = mapper.readValue<NewsArticle>(message.body)

        logger.info(
            "received article with id {} title {}",
            article.id,
            article.title,
        )
        try {
            logger.info("Article in queue {}", article.title)
            analysisService.analyzeAndSend(article)
            Thread.sleep(1000L)
            channel?.basicAck(message.envelope.deliveryTag, true)
        } catch(ex: Exception) {
            ex.printStackTrace()
            channel?.basicReject(message.envelope.deliveryTag, true)
        }
    }

}
