package io.collective.start.analyzer

import io.collective.workflow.WorkFinder
import org.slf4j.LoggerFactory

class AnalysisWorkFinder : WorkFinder<AnalysisTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun findRequested(name: String): List<AnalysisTask> {
        logger.info("finding work.")

        val work = AnalysisTask("some info")

        return mutableListOf(work)
    }

    override fun markCompleted(info: AnalysisTask) {
        logger.info("marking work complete.")
    }
}