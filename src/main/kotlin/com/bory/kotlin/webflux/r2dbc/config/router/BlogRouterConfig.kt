package com.bory.kotlin.webflux.r2dbc.config.router

import com.bory.kotlin.webflux.r2dbc.handler.blog.BlogHandler
import org.springframework.context.annotation.*
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class BlogRouterConfig(private val blogHandler: BlogHandler) {
  @Bean
  fun blogRouter() = coRouter {
    "/blog".nest {
      GET("", blogHandler::getAll)
      GET("/{id}", blogHandler::get)
      POST("", blogHandler::insert)
      PUT("/{id}", blogHandler::update)
    }

    GET("/blogswithjoin", blogHandler::getAllWithJoin)
  }
}
