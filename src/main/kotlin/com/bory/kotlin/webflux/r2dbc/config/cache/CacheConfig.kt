package com.bory.kotlin.webflux.r2dbc.config.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.context.properties.*
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.*
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig(private val caffeineCacheTypeMap: CaffeineCacheTypeMap) {
  @Bean
  fun cacheManager(): SimpleCacheManager =
      SimpleCacheManager().apply {
        setCaches(
            caffeineCacheTypeMap.caches.entries.map(this@CacheConfig::createCaffeineCache)
        )
      }

  private fun createCaffeineCache(entry: Map.Entry<String, CaffeineCacheProps>) =
      CaffeineCache(entry.key,
          Caffeine.newBuilder()
              .recordStats()
              .expireAfterWrite(entry.value.expiration, TimeUnit.MINUTES)
              .maximumSize(entry.value.maximumSize)
              .build())
}

@ConstructorBinding
@ConfigurationProperties(prefix = "cache-config.caffeine")
class CaffeineCacheTypeMap(val caches: Map<String, CaffeineCacheProps>)

class CaffeineCacheProps(val expiration: Long, val maximumSize: Long)
