package test.collective.news

import io.collective.database.DatabaseTemplate
import io.collective.database.testDataSource
import io.collective.news.NewsDataGateway
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewsDataGatewayTest {
    private val dataSource = testDataSource()

    @Before
    fun before() {
        DatabaseTemplate(dataSource).apply {
            execute("delete from news_articles")
            execute("insert into news_articles(source_id, source_name, title, description, content, url, image_url, published_at) values ('id1', 'Scientific American', 'Test Title', 'Test Description', 'Test Content','https://www.scientificamerican.com/article/blah', 'http://example.com/img1.jpg','2023-09-25T11:30:00Z')")
            execute("insert into news_articles(source_id, source_name, title, description, content, url, image_url, published_at) values ('cbs-news', 'CBS News', 'Test Title2', 'Test Description2', 'Test Content2','https://www.cbsnews.com/article/blah2', 'http://example.com/img2.jpg','2023-09-25T11:25:00Z')")
            execute("insert into news_articles(source_id, source_name, title, description, content, url, image_url, published_at) values ('abc-news-au', 'ABC News (AU)', 'Test Title3', 'Test Description3', 'Test Content3','https://www.abc.net.au/news/blah3', 'http://example.com/img3.jpg','2023-09-26T20:04:57Z')")
        }
    }

    @Test
    fun create() {
        val gateway = NewsDataGateway(dataSource)
        val publishDate = LocalDateTime.parse("2023-09-25T11:30:00Z", DateTimeFormatter.ISO_DATE_TIME)
        val newsArticle = gateway.create(
            UUID.randomUUID().toString(),
            "Scientific American",
            "Test Title",
            "Test Description",
            "Test Content",
            "https://www.scientificamerican.com/article/blah",
            "http://example.com/img1.jpg",
            publishedAt = publishDate
        )
        assertTrue(newsArticle.id > 0)
        assertEquals("Scientific American", newsArticle.sourceName)
        assertEquals("Test Title", newsArticle.title)
    }

    @Test
    fun selectAll() {
        val gateway = NewsDataGateway(dataSource)
        val newsArticles = gateway.findAll()
        assertEquals(3, newsArticles.size)
    }

    @Test
    fun getByDate() {
        val gateway = NewsDataGateway(dataSource)
        val newsArticle =
            gateway.findByDate(LocalDateTime.parse("2023-09-25T11:30:00Z", DateTimeFormatter.ISO_DATE_TIME))
        assertEquals("Scientific American", newsArticle?.sourceName)
    }
}
