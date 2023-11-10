package io.collective.messaging

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class BasicRabbitListener(
    private val rabbitUri: String,
    private val queue: String,
    private val delivery: ChannelDeliverCallback,
    private val cancel: CancelCallback,
    private val autoAck: Boolean = true
) {
    private var running = true
    private val connectionFactory = ConnectionFactory().apply { useBlockingIo() }

    fun start() {
        connectionFactory.setUri(rabbitUri)
        thread {
            connectionFactory.newConnection().use { connection ->
                connection.createChannel().use { channel ->
                    delivery.setChannel(channel)
                    while (running) {
                        try {
                            channel.basicQos(5)
                            channel.basicConsume(queue, autoAck, delivery, cancel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        sleep(100)
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
        sleep(100) // wait for a cycle, needed for testing
    }
}