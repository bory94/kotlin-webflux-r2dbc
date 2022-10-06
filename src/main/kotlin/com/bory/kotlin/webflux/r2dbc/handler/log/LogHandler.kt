package com.bory.kotlin.webflux.r2dbc.handler.log

import com.bory.kotlin.webflux.r2dbc.domain.Log
import com.bory.kotlin.webflux.r2dbc.repository.log.LogRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.*
import reactor.core.publisher.Mono

@Service
class LogHandler(private val logRepository: LogRepository) {
  companion object {
    private val LOGGER: Logger = LoggerFactory.getLogger(LogHandler::class.java)
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, noRollbackFor = [Throwable::class])
  suspend fun log(message: String): Log? = try {
    logRepository.save(Log.of(message)).awaitSingle()
  } catch (t: Throwable) {
    null
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, noRollbackFor = [Throwable::class])
  fun error(message: String): Mono<Log> = try {
    logRepository.save(Log.of("[ERROR] $message"))
  } catch (t: Throwable) {
    LOGGER.error("Error Occurred while logging error.", t)
    Mono.empty()
  }
}
