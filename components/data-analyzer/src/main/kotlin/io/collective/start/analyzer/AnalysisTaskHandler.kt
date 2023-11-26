package io.collective.start.analyzer

import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import io.collective.news.NewsArticle
import org.slf4j.LoggerFactory

class AnalysisTaskHandler(registry: MetricRegistry, private val analysisService: AnalysisService) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper().registerKotlinModule()
    private var channel: Channel? = null
    private val analysisRequests = registry.meter("article-analysis-requests")
    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String?, message: Delivery) {
        val article = mapper.readValue<NewsArticle>(message.body)

        logger.info(
            "received article with id {} title {}",
            article.sourceId,
            article.title,
        )
        try {
            logger.info("Article in queue {}", article.title)
            analysisService.analyzeAndSend(article)
            analysisRequests.mark()
            channel?.basicAck(message.envelope.deliveryTag, true)
        } catch(ex: Exception) {
            ex.printStackTrace()
            channel?.basicReject(message.envelope.deliveryTag, false)
        }
    }

}
