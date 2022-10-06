package com.bory.kotlin.webflux.r2dbc.cache.aspect

import com.bory.kotlin.webflux.r2dbc.cache.ReactiveCacheManager
import com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheEvict
import com.bory.kotlin.webflux.r2dbc.cache.generator.ReactorCacheKeyGenerator
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Aspect
@Component
class ReactorCacheEvictAspect(
    private val reactiveCacheManager: ReactiveCacheManager,
    private val reactorCacheKeyGenerator: ReactorCacheKeyGenerator
) {
  @Pointcut("@annotation(com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheEvict)")
  fun reactorCacheEvictPointcut() {
    // Pointcut method declaration
  }

  @Around("reactorCacheEvictPointcut()")
  fun around(joinPoint: ProceedingJoinPoint): Any {
    val method = (joinPoint.signature as MethodSignature).method
    val reactorCacheableAnnotation = method.getAnnotation(ReactorCacheEvict::class.java)
    val cacheName = reactorCacheableAnnotation.name
    val key = reactorCacheKeyGenerator.generateKey(joinPoint, reactorCacheableAnnotation.key)

    reactiveCacheManager.evict(cacheName, key)

    return joinPoint.proceed(joinPoint.args)
  }

}
