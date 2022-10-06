package com.bory.kotlin.webflux.r2dbc.cache.aspect

import com.bory.kotlin.webflux.r2dbc.cache.ReactiveCacheManager
import com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCachePut
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
class ReactorCachePutAspect(
    private val reactiveCacheManager: ReactiveCacheManager,
    private val reactorCacheKeyGenerator: ReactorCacheKeyGenerator
) {
  @Pointcut("@annotation(com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCachePut)")
  fun reactorCachePutPointcut() {
    // Pointcut method declaration
  }

  @Around("reactorCachePutPointcut()")
  fun around(joinPoint: ProceedingJoinPoint): Any {
    val method = (joinPoint.signature as MethodSignature).method
    val (publisherType, actualTypeInPublisher, reactorCachePutAnnotation) = getCachePutMethodSpec(method)

    return when (publisherType) {
      Mono::class.java -> mono(joinPoint, actualTypeInPublisher, reactorCachePutAnnotation)
      Flux::class.java -> flux(joinPoint, reactorCachePutAnnotation)
      else -> throw throw IllegalArgumentException("Return Type should be Flux or Mono.")
    }
  }

  private fun flux(joinPoint: ProceedingJoinPoint, reactorCachePut: ReactorCachePut): Flux<Any> {
    val key = reactorCacheKeyGenerator.generateKey(joinPoint, reactorCachePut.key)

    val flux = reactiveCacheManager
        .putCachedFlux(reactorCachePut.name, key) {
          joinPoint.proceed(joinPoint.args) as Flux<Any>
        }

    return if (reactorCachePut.blocking) flux.subscribeOn(Schedulers.boundedElastic()) else flux
  }

  private fun mono(joinPoint: ProceedingJoinPoint, actualTypeInMono: Class<Any>, reactorCachePut: ReactorCachePut): Mono<Any> {
    val key = reactorCacheKeyGenerator.generateKey(joinPoint, reactorCachePut.key)

    val mono = reactiveCacheManager
        .putCachedMono(reactorCachePut.name, key, actualTypeInMono) {
          joinPoint.proceed(joinPoint.args) as Mono<Any>
        }

    return if (reactorCachePut.blocking) mono.subscribeOn(Schedulers.boundedElastic()) else mono
  }

  private fun getCachePutMethodSpec(method: Method) =
      (method.genericReturnType as ParameterizedType)
          .let { parameterizedType ->
            Triple(
                parameterizedType.rawType,
                ResolvableType.forType(parameterizedType.actualTypeArguments[0]).resolve() as Class<Any>,
                method.getAnnotation(ReactorCachePut::class.java)
            )
          }

}
