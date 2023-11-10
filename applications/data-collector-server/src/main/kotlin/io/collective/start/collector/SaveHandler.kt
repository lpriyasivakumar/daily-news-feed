package io.collective.start.collector

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import io.collective.news.NewsArticle
import io.collective.news.NewsService
import org.slf4j.LoggerFactory


class  SaveHandler(private val newsService: NewsService): ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper().registerKotlinModule()
    private var channel: Channel? = null

    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String, message: Delivery) {
        val article = mapper.readValue<NewsArticle>(message.body)

        logger.info(
            "received article with id {} title {}",
            article.id,
            article.title,
        )
        try {
            newsService.save(article)
            Thread.sleep(1000L)
            channel?.basicAck(message.envelope.deliveryTag, true)
        } catch(ex: Exception) {
            ex.printStackTrace()
            channel?.basicReject(message.envelope.deliveryTag, true)
        }
    }
}