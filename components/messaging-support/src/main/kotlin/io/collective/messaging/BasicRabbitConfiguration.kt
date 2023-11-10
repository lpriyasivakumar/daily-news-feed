package io.collective.messaging

import com.rabbitmq.client.ConnectionFactory

class BasicRabbitConfiguration(private val rabbitUri: String, private val exchange: String, private val queue: String, private val routingKey: String) {
    fun setUp() {
        val connectionFactory = ConnectionFactory()
        connectionFactory.setUri(rabbitUri)
        val connection = connectionFactory.newConnection()

        connection.createChannel().use { channel ->
            channel.exchangeDeclare(exchange, "direct", false, false, null)
            channel.queueDeclare(queue, false, false, false, null)
            channel.queueBind(queue, exchange, routingKey)
        }
    }
}