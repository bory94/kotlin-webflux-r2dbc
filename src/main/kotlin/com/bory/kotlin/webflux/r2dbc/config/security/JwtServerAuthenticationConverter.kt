package com.bory.kotlin.webflux.r2dbc.config.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class JwtServerAuthenticationConverter : ServerAuthenticationConverter {
  override fun convert(exchange: ServerWebExchange): Mono<Authentication> =
      exchange.toMono()
          .map { it.request.headers["Authorization"] ?: listOf() }
          .filter { it.isNotEmpty() }
          .map { tokens ->
            val jwt = if (tokens[0].startsWith("Bearer", true)) tokens[0].substring(7)
            else tokens[0]

            UsernamePasswordAuthenticationToken(jwt, jwt)
          }
}
