package io.collective.start.collector

import io.collective.workflow.WorkFinder
import org.slf4j.LoggerFactory

class CollectionWorkFinder : WorkFinder<CollectionTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val url = "https://newsdata.io/api/1/news"
    private val apiKey = "pub_299780508defec537c308f38a7c30fe4465d4"

    override fun findRequested(name: String): List<CollectionTask> {
        logger.info("finding work.")

        val work = CollectionTask(url, apiKey)

        return mutableListOf(work)
    }

    override fun markCompleted(info: CollectionTask) {
        logger.info("marking work complete.")
    }
}