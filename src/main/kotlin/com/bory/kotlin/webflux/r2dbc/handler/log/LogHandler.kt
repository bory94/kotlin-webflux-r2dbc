package com.bory.kotlin.webflux.r2dbc.handler.log

import com.bory.kotlin.webflux.r2dbc.domain.Log
import com.bory.kotlin.webflux.r2dbc.repository.log.LogRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class LogHandler(private val logRepository: LogRepository) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LogHandler::class.java)
    }

    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        readOnly = false,
        noRollbackFor = [Throwable::class]
    )
    suspend fun log(message: String): Log? = try {
        logRepository.save(Log.of(message)).awaitSingle()
    } catch (t: Throwable) {
        null
    }

    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        readOnly = false,
        noRollbackFor = [Throwable::class]
    )
    fun error(message: String): Mono<Log> = try {
        logRepository.save(Log.of("[ERROR] $message"))
    } catch (t: Throwable) {
        LOGGER.error("Error Occurred while logging error.", t)
        Mono.empty()
    }
}
