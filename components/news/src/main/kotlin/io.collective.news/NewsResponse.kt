package io.collective.news

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewsResponse(
    val status: String,
    val results: List<NewsArticle>
)
