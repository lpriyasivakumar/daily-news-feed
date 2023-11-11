package io.collective.messaging

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory


class BasicRabbitConfiguration(private val rabbitUri: String,
                               private val exchange: String,
                               private val queue: String,
                               private val routingKey: String,
                               private val subscribe: Boolean,
                               private val callback: ChannelDeliverCallback?
) {
    fun setUp() {
        val connectionFactory = ConnectionFactory().apply { useBlockingIo() }
        connectionFactory.setUri(rabbitUri)
        connectionFactory.setConnectionTimeout(30000)
        val connection = connectionFactory.newConnection()

        val channel = connection.createChannel()
        channel.exchangeDeclare(exchange, "direct", false, false, null)
        channel.queueDeclare(queue, false, false, false, null)
        channel.queueBind(queue, exchange, routingKey)
        if (subscribe) {
            connectionFactory.isAutomaticRecoveryEnabled = true
            callback?.setChannel(channel)
            channel.basicConsume(queue, false, callback, CancelHandler())
        }
    }
}