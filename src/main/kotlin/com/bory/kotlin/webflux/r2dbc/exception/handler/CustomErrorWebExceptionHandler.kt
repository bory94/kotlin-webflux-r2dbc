package com.bory.kotlin.webflux.r2dbc.exception.handler

import com.bory.kotlin.webflux.r2dbc.handler.log.LogHandler
import org.slf4j.*
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.*
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
@Order(-2)
class CustomErrorWebExceptionHandler(val errorAttributes: CustomErrorAttributes,
                                     resources: WebProperties.Resources,
                                     applicationContext: ApplicationContext,
                                     serverCodecConfigurer: ServerCodecConfigurer,
                                     private val logHandler: LogHandler)
  : AbstractErrorWebExceptionHandler(errorAttributes, resources, applicationContext) {
  companion object {
    private val LOGGER: Logger = LoggerFactory.getLogger(CustomErrorWebExceptionHandler::class.java)
  }

  init {
    super.setMessageWriters(serverCodecConfigurer.writers)
    super.setMessageReaders(serverCodecConfigurer.readers)
  }

  override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> =
      RouterFunctions.route(RequestPredicates.all()) { request ->
        Mono.just(getErrorAttributes(request, ErrorAttributeOptions.defaults()))
            .flatMap { map ->
              if (map["status"] == HttpStatus.INTERNAL_SERVER_ERROR) {
                LOGGER.debug("logging internal server error: $map")
                logHandler.error(map.toString()).map { map }
              } else map.toMono()
            }
            .flatMap { map ->
              ServerResponse
                  .status(map["status"] as HttpStatus)
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(fromValue(map))
            }
      }
}
