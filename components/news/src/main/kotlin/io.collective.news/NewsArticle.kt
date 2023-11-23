package io.collective.news

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewsArticle(
    @JsonIgnore
    val id: Long,
    @JsonProperty("article_id")
    val sourceId: String,
    @JsonProperty("source_id")
    val sourceName: String,
    var title: String,
    val description: String?,
    val content: String?,
    @JsonProperty("link")
    val url: String,
    @JsonProperty("image_url")
    val imageUrl: String?,
    @JsonProperty("sentiment")
    var sentiment: Number = -1,
    @JsonProperty("pubDate")
    val publishedAt: String
)

fun NewsRecord.toDto() = NewsArticle(
    id = id,
    sourceId = sourceId,
    sourceName = sourceName,
    title = title,
    description = description,
    content = content,
    url = url,
    imageUrl = imageUrl,
    sentiment = sentiment,
    publishedAt = publishedAt.toString()
)