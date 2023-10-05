package io.collective.start.analyzer

import io.collective.workflow.Worker
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class AnalysisWorker(override val name: String = "data-analyzer") : Worker<AnalysisTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun execute(task: AnalysisTask) {
        runBlocking {
            logger.info("starting data analysis.")

            // todo - data analysis happens here

            logger.info("completed data analysis.")
        }
    }
}