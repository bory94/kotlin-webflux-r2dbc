package com.bory.kotlin.webflux.r2dbc.exception.handler

import com.bory.kotlin.webflux.r2dbc.exception.GenericException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException

@Component
class CustomErrorAttributes : DefaultErrorAttributes() {
  override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
    val map = super.getErrorAttributes(request, options)

    when (val throwable = getError(request)) {
      is GenericException -> {
        map["status"] = throwable.status
        map["message"] = throwable.message
      }
      is ResponseStatusException -> {
        map["status"] = throwable.status
        map["message"] = throwable.message
      }
      else -> {
        map["message"] = throwable.message
        map["exception"] = throwable.javaClass.simpleName
        map["status"] = HttpStatus.INTERNAL_SERVER_ERROR
      }
    }

    return map
  }
}
