package com.bory.kotlin.webflux.r2dbc.config.router

import com.bory.kotlin.webflux.r2dbc.handler.account.*
import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.*
import org.springframework.web.reactive.function.server.*

@Configuration
class AccountRouterConfig(
    private val accountHandler: AccountHandler,
    private val loginHandler: LoginHandler
) {
  @FlowPreview
  @Bean
  fun accountRouter(): RouterFunction<ServerResponse> = coRouter {
    "/account".nest {
      GET("", accountHandler::getAllWithCount)
      GET("/{id}", accountHandler::get)
      POST("", accountHandler::insert)
      PUT("/{id}", accountHandler::update)
    }

    "/login".nest {
      GET("/current", accountHandler::current)
      POST("", loginHandler::login)
    }

    POST("/token/refresh", loginHandler::refreshAccessToken)
  }
}
