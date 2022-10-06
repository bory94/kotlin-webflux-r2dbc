package com.bory.kotlin.webflux.r2dbc.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("log")
data class Log(
    @Id
    var id: Long?,
    var message: String?,
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
  companion object {
    fun of(message: String) = Log(null, message)
  }
}

