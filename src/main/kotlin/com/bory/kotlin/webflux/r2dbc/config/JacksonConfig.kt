package com.bory.kotlin.webflux.r2dbc.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.*

@Configuration
class JacksonConfig(private val objectMapper: ObjectMapper) {
  @Bean
  fun objectMapperConfigurer() =
      ApplicationRunner {
        objectMapper.apply {
          configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
          setSerializationInclusion(JsonInclude.Include.NON_NULL)
          registerModule(JavaTimeModule())
          disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
      }
}
