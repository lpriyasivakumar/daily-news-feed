package io.collective.database

import javax.sql.DataSource

const val testJdbcUrl = "jdbc:postgresql://localhost:5555/news_test"
const val testDbUsername = "news-reader"
const val testDbPassword = "news-reader"

fun testDataSource(): DataSource {
    return createDatasource(
        jdbcUrl = testJdbcUrl,
        username = testDbUsername,
        password = testDbPassword
    )
}
