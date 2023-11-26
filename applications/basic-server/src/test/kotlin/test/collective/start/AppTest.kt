package test.collective.start

import com.codahale.metrics.Gauge
import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import io.collective.start.module
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.prometheus.client.CollectorRegistry
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class AppTest {
    @Test
    fun testEmptyHome() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertTrue(response.content!!.contains("Simple input echoer"))
        }
    }

    @Test
    fun testEcho() = testApp {
        handleRequest(HttpMethod.Post, "/echo") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("user_input" to "hello").formUrlEncode())
        }.apply {
            assertEquals(200, response.status()?.value)
            assertTrue(response.content!!.contains("hello"))
        }
    }

    @Test
    fun testNewsFeed() = testApp {
        handleRequest(HttpMethod.Get, "/news-feed"){
            parametersOf("filterby","all")
        }.apply {
            assertEquals(200, response.status()?.value)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        val mockCollector = mockk<CollectorRegistry> {
            every { register(any()) } returns Unit
        }
        val mockRegistry = mockk<MetricRegistry> {
            every { meter(any()) } returns Meter()
            every { getGauges(any()) } returns TreeMap<String, Gauge<Any>>()
        }
        val client = HttpClient(MockEngine) {
            install(JsonFeature) { serializer = JacksonSerializer()}
            engine {
                addHandler { _: HttpRequestData ->
                    respond(
                        content = "[]",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }
        withTestApplication({
            module(
                registry = mockRegistry,
                collectorRegistry = mockCollector,
                client =client
            )
        }) { callback() }
    }
}
