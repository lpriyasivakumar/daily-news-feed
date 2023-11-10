package io.collective.messaging

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

class BasicDispatcher(
    private val rabbitUri: String,
    private val exchange: String,
    private val messageBody: ByteArray,
    private val routingKey: String,
){
    private val factory = ConnectionFactory().apply { useNio() }
     fun send(){
         factory.setUri(rabbitUri)
         factory.setConnectionTimeout(30000)
         factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_BASIC, messageBody)
            }
        }
    }

}