package com.bory.kotlin.webflux.r2dbc.cache

import com.bory.kotlin.webflux.r2dbc.exception.CacheNotFoundException
import org.springframework.cache.*
import org.springframework.stereotype.Component
import reactor.cache.*
import reactor.core.publisher.*
import java.util.function.Supplier

@Component
@Suppress("UNCHECKED_CAST")
class ReactiveCacheManager(private val cacheManager: CacheManager) {
  fun <T> cachedMono(cacheName: String, key: Any, clazz: Class<T>, supplier: Supplier<Mono<T>>): Mono<T> =
      getCache(cacheName).let { cache ->
        forMono(cache, key, supplier, Mono.justOrEmpty(cache.get(key, clazz)))
      }

  fun <T> putCachedMono(cacheName: String, key: Any, clazz: Class<T>, supplier: Supplier<Mono<T>>): Mono<T> =
      forMono(getCache(cacheName), key, supplier, Mono.empty())

  fun <T> cachedFlux(cacheName: String, key: Any, supplier: Supplier<Flux<T>>): Flux<T> =
      getCache(cacheName).let { cache ->
        forFlux(cache, key, supplier, Mono.justOrEmpty(cache.get(key, List::class.java)).map { it as List<T> })
      }

  fun <T> putCachedFlux(cacheName: String, key: Any, supplier: Supplier<Flux<T>>): Flux<T> =
      forFlux(getCache(cacheName), key, supplier, Mono.empty())

  fun evict(cacheName: String, key: Any) = getCache(cacheName).evict(key)
  fun clear(cacheName: String) = getCache(cacheName).clear()

  private fun getCache(cacheName: String) = cacheManager.getCache(cacheName)
      ?: throw CacheNotFoundException("Cache[$cacheName] not found.")

  private fun <T> forMono(cache: Cache, key: Any, supplier: Supplier<Mono<T>>, source: Mono<T>): Mono<T> =
      CacheMono.lookup({ source.map { Signal.next(it) } }, key)
          .onCacheMissResume(Mono.defer(supplier))
          .andWriteWith { k, signal ->
            Mono.fromRunnable {
              if (!signal.isOnError) {
                cache.put(k, signal.get())
              }
            }
          }

  private fun <T> forFlux(cache: Cache, key: Any, supplier: Supplier<Flux<T>>, source: Mono<List<T>>): Flux<T> =
      CacheFlux.lookup({
        source.flatMap { list -> Flux.fromIterable(list).materialize().collectList() }
      }, key)
          .onCacheMissResume(supplier)
          .andWriteWith { k, signal ->
            Flux.fromIterable(signal)
                .dematerialize<List<T>>()
                .collectList()
                .doOnNext { cache.put(k, it) }
                .then()
          }
}
