package io.collective.news

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
  id                  serial primary key,
    source_id           varchar,
    source_name         varchar not null,
    title               varchar not null,
    description         varchar not null,
    content             varchar not null,
    url                 varchar not null,
    image_url           varchar not null,
    sentiment           smallInt default -1,
    published_at        timestamp default current_timestamp

 */
data class NewsRecord(
    val id: Long,
    val sourceId: String,
    val sourceName: String,
    var title: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String,
    val sentiment: Number,
    val publishedAt: LocalDateTime
)

fun NewsArticle.toRecord() = NewsRecord(
    id = id,
    sourceId = sourceId,
    sourceName = sourceName,
    title = title,
    description = description ?: "",
    content = content ?: "",
    url = url,
    imageUrl = imageUrl ?: "",
    sentiment = sentiment ?: -1,
    publishedAt = LocalDateTime.parse(publishedAt, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
)