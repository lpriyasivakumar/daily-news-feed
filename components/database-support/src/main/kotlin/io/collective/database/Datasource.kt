package io.collective.database

import javax.sql.DataSource

val jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5555/news_dev"
val dbUsername = System.getenv("JDBC_USER") ?: "news-reader"
val dbPassword = System.getenv("JDBC_PASSWORD") ?: "news-reader"

fun dataSource(): DataSource {
    return createDatasource(
        jdbcUrl = jdbcUrl,
        username = dbUsername,
        password = dbPassword
    )
}