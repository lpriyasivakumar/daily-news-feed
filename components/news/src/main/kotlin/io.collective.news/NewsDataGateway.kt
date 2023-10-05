package io.collective.news

import io.collective.database.DatabaseTemplate
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

class NewsDataGateway(private val dataSource: DataSource) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val template = DatabaseTemplate(dataSource)

    fun create(
        sourceId: String?,
        sourceName: String,
        title: String,
        description: String,
        content: String,
        url: String,
        imageUrl: String,
        publishedAt: LocalDateTime
    ): NewsRecord {
        println(publishedAt.toString())
        println(Timestamp.valueOf(publishedAt))
        return template.create(
            """insert into news_articles ( 
                                    source_id,
                                    source_name,
                                    title,
                                    description,
                                    content,
                                    url,
                                    image_url,
                                    published_at) values (?,?,?,?,?,?,?,?)""", { id ->
                NewsRecord(id, sourceId ?: "", sourceName, title, description, content, url, imageUrl, publishedAt)
            }, sourceId ?: ' ', sourceName, title, description, content, url, imageUrl, publishedAt.toString()
        )
    }

    fun findAll(): List<NewsRecord> {
        return template.findAll("select * from news_articles order by id") { rs ->
            NewsRecord(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getString(7),
                rs.getString(8),
                LocalDateTime.parse(rs.getString(9), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
            )
        }
    }

    fun findBy(id: Long): NewsRecord? {
        return template.findBy(
            "select * from news_articles where id = ?", { rs ->
                NewsRecord(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    LocalDateTime.parse(rs.getString(9), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                )
            }, id
        )
    }

    fun findByDate(date: LocalDateTime): NewsRecord? {
        return template.findByField(
            "select * from news_articles where published_at = ?", { rs ->
                NewsRecord(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    LocalDateTime.parse(rs.getString(9), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                )
            }, date.toString()
        )
    }
}