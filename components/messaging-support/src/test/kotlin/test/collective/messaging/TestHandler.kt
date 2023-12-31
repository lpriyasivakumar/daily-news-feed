package test.collective.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import org.slf4j.LoggerFactory

class TestHandler(private val name: String, private val function: () -> Unit) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun setChannel(channel: Channel) {
    }

    override fun handle(consumerTag: String, message: Delivery) {
        logger.info("handling '${String(message.body)}' on channel=$name")
        function()
    }
}