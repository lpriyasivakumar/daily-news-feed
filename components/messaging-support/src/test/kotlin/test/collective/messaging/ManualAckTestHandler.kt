package test.collective.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.collective.messaging.ChannelDeliverCallback
import org.slf4j.LoggerFactory

class ManualAckTestHandler(private val name: String, private val function: () -> Unit) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var channel: Channel? = null

    override fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun handle(consumerTag: String, message: Delivery) {
        logger.info("manually handling '${String(message.body)}' on channel=$name")
        function()
        channel!!.basicAck(message.envelope.deliveryTag, true)
    }
}