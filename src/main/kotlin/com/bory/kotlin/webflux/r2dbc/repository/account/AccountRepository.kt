package com.bory.kotlin.webflux.r2dbc.repository.account

import com.bory.kotlin.webflux.r2dbc.cache.annotation.*
import com.bory.kotlin.webflux.r2dbc.domain.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AccountRepository : ReactiveCrudRepository<Account, Long>, AccountRepositoryCustom {
  fun existsByEmail(email: String): Mono<Boolean>
  fun existsByEmailAndIdNot(email: String, id: Long): Mono<Boolean>
  fun findByEmail(email: String): Mono<Account>

  @ReactorCacheable("account", key = "#id")
  override fun findById(id: Long): Mono<Account>

  @ReactorCacheEvict("account", "#account.id")
  fun save(account: Account): Mono<Account>
}

interface AccountRepositoryCustom {
  fun findAccountWithRolesByEmail(email: String): Mono<AccountWithRoles>
  fun updateRefreshToken(email: String, refreshToken: String): Mono<Int>
}
