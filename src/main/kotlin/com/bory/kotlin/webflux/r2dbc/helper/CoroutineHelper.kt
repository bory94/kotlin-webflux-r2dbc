package com.bory.kotlin.webflux.r2dbc.helper

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CoroutineHelper {
  companion object {
    suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T =
        asyncAwait(Dispatchers.Default) { block.invoke(this) }

    suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> =
        GlobalScope.async { block.invoke(this) }

    suspend fun <T> asyncAwait(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
        withContext(context) { block.invoke(this) }
  }
}
