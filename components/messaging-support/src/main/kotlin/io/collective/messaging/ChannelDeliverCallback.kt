package io.collective.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.DeliverCallback

interface ChannelDeliverCallback: DeliverCallback {
    fun setChannel(channel: Channel)
}