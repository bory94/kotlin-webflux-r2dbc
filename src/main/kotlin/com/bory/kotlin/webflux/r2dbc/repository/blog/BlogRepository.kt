package com.bory.kotlin.webflux.r2dbc.repository.blog

import com.bory.kotlin.webflux.r2dbc.cache.annotation.*
import com.bory.kotlin.webflux.r2dbc.domain.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.*

interface BlogRepository : ReactiveCrudRepository<Blog, Long>, BlogRepositoryCustom {
  fun findAllByOrderByIdDesc(): Flux<Blog>
  fun findByCreatedBy(createdBy: Long): Flux<Blog>

  fun countByCreatedBy(createdBy: Long): Mono<Long>

  @ReactorCacheEvict(name = "blog")
  fun save(blog: Blog): Mono<Blog>
}

interface BlogRepositoryCustom {
  @ReactorCacheable("blog")
  fun findAllWithJoin(): Flux<BlogWithAccount>
}
