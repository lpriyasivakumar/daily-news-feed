package io.collective.messaging

import com.rabbitmq.client.ConnectionFactory


class BasicRabbitConfiguration(private val rabbitUri: String, private val exchange: String, private val queue: String, private val routingKey: String) {
    fun setUp() {
        val connectionFactory = ConnectionFactory()
        connectionFactory.setUri(rabbitUri)
        connectionFactory.setConnectionTimeout(30000)
        val connection = connectionFactory.newConnection()

        connection.createChannel().use { channel ->
            channel.exchangeDeclare(exchange, "direct", false, false, null)
            val arguments: MutableMap<String, Any> = HashMap()
            arguments["x-single-active-consumer"] = true
            channel.queueDeclare(queue, false, false, false, arguments)
            channel.queueBind(queue, exchange, routingKey)
        }
    }
}