package com.bory.kotlin.webflux.r2dbc.config.security

import com.bory.kotlin.webflux.r2dbc.domain.*
import com.bory.kotlin.webflux.r2dbc.helper.JwtHelper
import org.springframework.security.authentication.*
import org.springframework.security.core.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class JwtAuthenticationManager(private val jwtHelper: JwtHelper) : ReactiveAuthenticationManager {

  override fun authenticate(authentication: Authentication): Mono<Authentication> =
      authentication.toMono()
          .map { jwtHelper.validateToken(it.credentials as String) }
          .doOnError { Mono.error<AuthenticationException>(it) }
          .map { jwt ->
            val accountWithRoles = AccountWithRoles.fromJwsClaims(jwt)
            UsernamePasswordAuthenticationToken(
                Account.fromAccountWithRoles(accountWithRoles),
                authentication.credentials as String,
                accountWithRoles.roles.map { GrantedAuthority { it.name } }.toMutableList()
            )
          }
}
