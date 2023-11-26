package test.collective.messaging

import com.rabbitmq.client.CancelCallback

class TestCancelHandler : CancelCallback {
    override fun handle(consumerTag: String?) {
    }
}