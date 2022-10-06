package com.bory.kotlin.webflux.r2dbc.config

import org.springframework.context.annotation.*
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.*

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
  @Bean
  fun securityWebFilterChain(
      http: ServerHttpSecurity,
      jwtAuthenticationManager: ReactiveAuthenticationManager,
      jwtAuthenticationConverter: ServerAuthenticationConverter
  ): SecurityWebFilterChain {
    val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        .apply { setServerAuthenticationConverter(jwtAuthenticationConverter) }

    return http.authorizeExchange()
        .pathMatchers("/signup", "/login", "/token/refresh", "/actuator/**").permitAll()
        .pathMatchers("/**").authenticated()
        .and()
        .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic().disable()
        .csrf().disable()
        .formLogin().disable()
        .logout().disable()
        .build()
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
