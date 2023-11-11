package io.collective.messaging

import com.rabbitmq.client.CancelCallback

class CancelHandler: CancelCallback {
    override fun handle(consumerTag: String) {
    }
}