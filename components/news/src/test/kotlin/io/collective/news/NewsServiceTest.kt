package io.collective.news

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class NewsServiceTest {
    private val gateway = mockk<NewsDataGateway>()
    private val service = NewsService(gateway)

    private val record1 = NewsRecord(
        id = 1L,
        sourceId = "newscnn",
        sourceName = "cnn",
        title = "Breaking news happening",
        content = "blah!!",
        description = "test description",
        imageUrl = "http://exampleimg.com",
        url = "http://newslinkccn.com",
        sentiment = 3,
        publishedAt = LocalDateTime.parse("2023-10-07 02:15:29", DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
    )
    private val record2 = NewsRecord(
        id = 2L,
        sourceId = "newsabc",
        sourceName = "abc",
        title = "Breaking news happening ABC",
        content = "More blah!!",
        description = "",
        imageUrl = "http://exampleimg2.com",
        url = "http://newslinkabc.com",
        sentiment = 1,
        publishedAt = LocalDateTime.parse("2023-10-07 02:15:29", DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
    )
    @Test
    fun testFindAllEmpty(){
        every {gateway.findAll()} returns listOf()
        val records = service.findAll()
        assertTrue(records.isEmpty())
    }

    @Test
    fun testFindAllWithData() {
        every { gateway.findAll() } returns listOf(record1, record2)
        val newsArticles = service.findAll()
        assertEquals(2, newsArticles.size)
        assertEquals("cnn", newsArticles[0].sourceName)
        assertEquals("abc", newsArticles[1].sourceName)
    }

    @Test
    fun testFindBySentiment() {
        every { gateway.findBySentiment("positive") } returns listOf(record2)
        val newsArticles = service.findAllBySentiment("positive")
        assertEquals(1, newsArticles.size)
        assertEquals("abc", newsArticles[0].sourceName)
    }

    @Test
    fun testFindById() {
        every { gateway.findBy(1L) } returns record1
        val newsArticle = service.findBy(1L)
        assertEquals("cnn", newsArticle.sourceName)
    }

}