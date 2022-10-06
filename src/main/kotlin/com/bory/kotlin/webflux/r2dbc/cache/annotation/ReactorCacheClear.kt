package com.bory.kotlin.webflux.r2dbc.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ReactorCacheClear(
    val name: String
)
