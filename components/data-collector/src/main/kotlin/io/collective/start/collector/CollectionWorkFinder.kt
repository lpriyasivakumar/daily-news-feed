package io.collective.start.collector

import io.collective.workflow.WorkFinder
import org.slf4j.LoggerFactory

class CollectionWorkFinder : WorkFinder<CollectionTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val url = "https://newsdata.io/api/1/news"
    private val apiKey = " pub_2997838abbdd93eaa60c52265abb58ce0d090"

    override fun findRequested(name: String): List<CollectionTask> {
        logger.info("finding work.")

        val work = CollectionTask(url, apiKey)

        return mutableListOf(work)
    }

    override fun markCompleted(info: CollectionTask) {
        logger.info("marking work complete.")
    }
}