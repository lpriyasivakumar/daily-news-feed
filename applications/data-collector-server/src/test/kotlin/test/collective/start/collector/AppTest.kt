package test.collective.start.collector

import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import io.collective.start.collector.module
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.prometheus.client.CollectorRegistry
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class AppTest {

    @Test
    fun testEmptyHome() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertTrue(response.content!!.contains("hi!"))
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        val mockCollector = mockk<CollectorRegistry> {
            every { register(any()) } returns Unit
        }
        val mockRegistry = mockk<MetricRegistry> {
            every { meter(any()) } returns Meter()
        }
        withTestApplication({
            module(
                registry = mockRegistry,
                collectorRegistry = mockCollector
            )
        }) { callback() }
    }
}