package com.bory.kotlin.webflux.r2dbc.cache.aspect

import com.bory.kotlin.webflux.r2dbc.cache.ReactiveCacheManager
import com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheable
import com.bory.kotlin.webflux.r2dbc.cache.generator.ReactorCacheKeyGenerator
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.*
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.*
import reactor.core.scheduler.Schedulers
import java.lang.reflect.*

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Aspect
@Component
@Suppress("UNCHECKED_CAST")
class ReactorCacheableAspect(
    private val reactiveCacheManager: ReactiveCacheManager,
    private val reactorCacheKeyGenerator: ReactorCacheKeyGenerator
) {
  @Pointcut("@annotation(com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheable)")
  fun reactorCacheablePointcut() {
    // Pointcut method declaration
  }

  @Around("reactorCacheablePointcut()")
  fun around(joinPoint: ProceedingJoinPoint): Any {
    val method = (joinPoint.signature as MethodSignature).method
    val (publisherType, actualTypeInPublisher, reactorCacheableAnnotation) = getCacheableMethodSpec(method)

    return when (publisherType) {
      Mono::class.java -> mono(joinPoint, actualTypeInPublisher, reactorCacheableAnnotation)
      Flux::class.java -> flux(joinPoint, reactorCacheableAnnotation)
      else -> throw throw IllegalArgumentException("Return Type should be Flux or Mono.")
    }
  }

  private fun flux(joinPoint: ProceedingJoinPoint, reactorCacheable: ReactorCacheable): Flux<Any> {
    val key = reactorCacheKeyGenerator.generateKey(joinPoint, reactorCacheable.key)

    val flux =
        reactiveCacheManager
            .cachedFlux(reactorCacheable.name, key) {
              joinPoint.proceed(joinPoint.args) as Flux<Any>
            }

    return if (reactorCacheable.blocking) flux.subscribeOn(Schedulers.boundedElastic()) else flux
  }

  private fun mono(joinPoint: ProceedingJoinPoint, actualTypeInMono: Class<Any>, reactorCacheable: ReactorCacheable): Mono<Any> {
    val key = reactorCacheKeyGenerator.generateKey(joinPoint, reactorCacheable.key)

    val mono =
        reactiveCacheManager
            .cachedMono(reactorCacheable.name, key, actualTypeInMono) {
              joinPoint.proceed(joinPoint.args) as Mono<Any>
            }

    return if (reactorCacheable.blocking) mono.subscribeOn(Schedulers.boundedElastic()) else mono
  }

  private fun getCacheableMethodSpec(method: Method) =
      (method.genericReturnType as ParameterizedType)
          .let { parameterizedType ->
            Triple(
                parameterizedType.rawType,
                ResolvableType.forType(parameterizedType.actualTypeArguments[0]).resolve() as Class<Any>,
                method.getAnnotation(ReactorCacheable::class.java)
            )
          }

}
