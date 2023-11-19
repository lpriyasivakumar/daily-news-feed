package io.collective.start.collector

import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import io.collective.news.NewsArticle
import io.collective.news.NewsService
import org.slf4j.LoggerFactory


class  SaveHandler(private val newsService: NewsService, registry: MetricRegistry): ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper().registerKotlinModule()
    private var channel: Channel? = null
    private val saveRequests = registry.meter("article-save-requests")
    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String, message: Delivery) {
        try {
            logger.info("Handling save message {}", message.properties)
            if (message.body != null) {
                val article = mapper.readValue<NewsArticle>(message.body)
                logger.info(
                    "received article with id {} title {}",
                    article.id,
                    article.title,
                )
                newsService.save(article)
                saveRequests.mark()
            }
            channel?.basicAck(message.envelope.deliveryTag, true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            channel?.basicReject(message.envelope.deliveryTag, false)
        }
    }
}