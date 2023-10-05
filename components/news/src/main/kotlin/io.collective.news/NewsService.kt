package io.collective.news

class NewsService(private val dataGateway: NewsDataGateway) {
    fun findAll(): List<NewsArticle> {
        return dataGateway.findAll().map { it.toDto() }
    }

    fun findBy(id: Long): NewsArticle {
        val record = dataGateway.findBy(id)!!
        return record.toDto()
    }

    fun save(newsArticle: NewsArticle): NewsArticle {
        val record = newsArticle.toRecord()
        val entity = dataGateway.create(
            record.sourceId,
            record.sourceName,
            record.title,
            record.description,
            record.content,
            record.url,
            record.imageUrl,
            record.publishedAt
        )
        return findBy(entity.id)
    }
}