package test.collective.start.analyzer

import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import com.rabbitmq.client.Envelope
import io.collective.news.NewsArticle
import io.collective.start.analyzer.AnalysisService
import io.collective.start.analyzer.AnalysisTaskHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class AnalysisTaskHandlerTest {
    private val mockChannel = mockk<Channel>()
    private val mockService = mockk<AnalysisService>()
    private val mockRegistry = mockk<MetricRegistry>()
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    private val testArticle = NewsArticle(
        id = 1L,
        sourceId = "newscnn",
        sourceName = "cnn",
        title = "Breaking news happening",
        content = "blah!!",
        description = "test description",
        imageUrl = "http://exampleimg.com",
        url = "http://newslinkccn.com",
        sentimentValue = 3,
        publishedAt = "2023-10-07 02:15:29"
    )

    @Before
    fun setup(){
        val meter = mockk<Meter>()
        every {meter.mark()} returns Unit
        every{mockRegistry.meter(any())} returns meter
        every{mockChannel.basicAck(any(), any())} returns Unit
        every{mockChannel.basicReject(any(), any())} returns Unit
    }
    @Test
    fun testHandleAndAck() {
        every{mockService.analyzeAndSend(any())} returns Unit
        val analysisTaskHandler = AnalysisTaskHandler(mockRegistry,mockService)
        val body = mapper.writeValueAsString(testArticle).toByteArray()
        val message = Delivery(
            Envelope(1L, false, "test-exchange","test-key"),
            AMQP.BasicProperties(),
            body
        )
        analysisTaskHandler.setChannel(mockChannel)
        //Test handle method
        analysisTaskHandler.handle("TestTag", message )
        //verify analysis is run
        verify {
            mockService.analyzeAndSend(withArg {
                assertEquals(testArticle.sourceName, it.sourceName)
        })}
        //verify message is acked
        verify {mockChannel.basicAck(1L, true)}
    }

    @Test
    fun testHandleAndReject() {
        every{mockService.analyzeAndSend(any())} throws Exception()
        val analysisTaskHandler = AnalysisTaskHandler(mockRegistry,mockService)
        val body = mapper.writeValueAsString(testArticle).toByteArray()
        val message = Delivery(
            Envelope(1L, false, "test-exchange","test-key"),
            AMQP.BasicProperties(),
            body
        )

        analysisTaskHandler.setChannel(mockChannel)

        //Test handle method
        analysisTaskHandler.handle("TestTag", message )
        //verify analysis is run
        verify {
            mockService.analyzeAndSend(withArg {
                assertEquals(testArticle.sourceName, it.sourceName)
            })}
        //verify message is acked
        verify {mockChannel.basicReject(1L, false)}
    }

}