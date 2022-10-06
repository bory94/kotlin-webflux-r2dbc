package com.bory.kotlin.webflux.r2dbc.cache.aspect

import com.bory.kotlin.webflux.r2dbc.cache.ReactiveCacheManager
import com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheClear
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Aspect
@Component
class ReactorCacheClearAspect(private val reactiveCacheManager: ReactiveCacheManager) {
  @Pointcut("@annotation(com.bory.kotlin.webflux.r2dbc.cache.annotation.ReactorCacheClear)")
  fun reactorCacheClearPointcut() {
    // Pointcut method declaration
  }

  @Around("reactorCacheClearPointcut()")
  fun around(joinPoint: ProceedingJoinPoint): Any {
    val method = (joinPoint.signature as MethodSignature).method
    val reactorCacheableAnnotation = method.getAnnotation(ReactorCacheClear::class.java)

    reactiveCacheManager.clear(reactorCacheableAnnotation.name)

    return joinPoint.proceed(joinPoint.args)
  }
}
