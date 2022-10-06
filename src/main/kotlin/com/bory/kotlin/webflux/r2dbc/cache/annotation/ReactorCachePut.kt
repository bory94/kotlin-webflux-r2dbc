package com.bory.kotlin.webflux.r2dbc.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ReactorCachePut(
    val name: String,
    val key: String = "",
    val blocking: Boolean = false
)
