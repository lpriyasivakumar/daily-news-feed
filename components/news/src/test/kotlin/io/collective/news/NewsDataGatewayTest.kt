package io.collective.news

import io.collective.database.testDataSource
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class NewsDataGatewayTest() {
    private val gateway = NewsDataGateway(testDataSource())

    @Before
    fun cleanDatabase() {
        gateway.delete()
    }

    @Test
    fun testCreate() {
        val savedRecord = gateway.create(
            sourceId = "newscnn",
            sourceName = "cnn",
            title="Breaking news happening",
            content="blah!!",
            description = "test description",
            imageUrl = "http://exampleimg.com",
            url="http://newslinkccn.com",
            sentiment = 3,
            publishedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        )

        val record = gateway.findBy(savedRecord.id)
        assertEquals("http://newslinkccn.com", record?.url)
    }

    @Test
    fun testFindAll() {
        gateway.create(
            sourceId = "newsabc",
            sourceName = "abc",
            title="Breaking news happening ABC",
            content="More blah!!",
            description = "",
            imageUrl = "http://exampleimg2.com",
            url="http://newslinkabc.com",
            sentiment = 1,
            publishedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        )
       gateway.create(
            sourceId = "newscnn",
            sourceName = "cnn",
            title="Breaking news happening",
            content="blah!!",
            description = "test description",
            imageUrl = "http://exampleimg.com",
            url="http://newslinkccn.com",
            sentiment = 3,
            publishedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        )

        val records = gateway.findAll()
        assertEquals(records.size, 2)
        assertEquals(records[0].sourceId, "newsabc")
        assertEquals(records[1].sourceId, "newscnn")
    }

    @Test
    fun testFindBySentiment() {
        gateway.create(
            sourceId = "newsabc",
            sourceName = "abc",
            title="Breaking news happening ABC",
            content="More blah!!",
            description = "",
            imageUrl = "http://exampleimg2.com",
            url="http://newslinkabc.com",
            sentiment = 0,
            publishedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        )

        val positiveNews = gateway.findBySentiment("positive")
        val negativeNews = gateway.findBySentiment("negative")
        val unknown = gateway.findBySentiment("not sure")
        assertEquals(positiveNews.size, 0)
        assertEquals(negativeNews.size, 1)
        assertEquals(unknown.size, 1)
        assertEquals(negativeNews[0].sourceName, "abc")
    }


}