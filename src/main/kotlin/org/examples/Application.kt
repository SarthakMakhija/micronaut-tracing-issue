package org.examples

import io.micronaut.runtime.Micronaut
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Application {
    private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Starting application")
        Micronaut.run()
    }
}