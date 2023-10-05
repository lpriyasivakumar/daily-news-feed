package io.collective.start.rest

import io.collective.database.dataSource
import io.collective.news.NewsArticle
import io.collective.news.NewsDataGateway
import io.collective.news.NewsService

class ApiInterface {
    private val dataGateway = NewsDataGateway(dataSource())
    private val newsService = NewsService(dataGateway)

    fun save(newsArticle: NewsArticle): NewsArticle {
        return newsService.save(newsArticle)
    }

    fun findAll(): List<NewsArticle> {
        return newsService.findAll()
    }
}