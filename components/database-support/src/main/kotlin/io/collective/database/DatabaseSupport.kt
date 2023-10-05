package io.collective.database

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource

fun createDatasource(
    jdbcUrl: String, username: String, password: String,
): DataSource = HikariDataSource().apply {
    setJdbcUrl(jdbcUrl)
    setUsername(username)
    setPassword(password)
    addDataSourceProperty("stringtype", "unspecified")
    transactionIsolation = Connection.TRANSACTION_READ_COMMITTED.toString()
}
