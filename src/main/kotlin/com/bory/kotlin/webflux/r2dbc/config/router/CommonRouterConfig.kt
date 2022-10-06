package com.bory.kotlin.webflux.r2dbc.config.router

import com.bory.kotlin.webflux.r2dbc.cache.ReactiveCacheManager
import org.springframework.context.annotation.*
import org.springframework.web.reactive.function.server.*

@Configuration
class CommonRouterConfig(
    private val reactiveCacheManager: ReactiveCacheManager
) {
  @Bean
  fun commonCoroutineRouter() = coRouter {
    GET("/cache/clear/{cacheName}") { serverRequest ->
      val cacheName = serverRequest.pathVariable("cacheName")
      reactiveCacheManager.clear(cacheName)
      ok().bodyValueAndAwait("Cache[$cacheName] cleared.")
    }

  }
}
