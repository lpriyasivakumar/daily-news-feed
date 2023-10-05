package io.collective.database

import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

class TransactionManager(private val dataSource: DataSource) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    fun <T> withTransaction(function: (Connection) -> T): T {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            val results = function(connection)
            connection.commit()
            connection.autoCommit = true
            return results
        }
    }
}
