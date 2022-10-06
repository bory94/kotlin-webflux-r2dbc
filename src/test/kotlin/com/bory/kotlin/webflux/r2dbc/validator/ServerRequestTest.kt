package com.bory.kotlin.webflux.r2dbc.validator

import com.bory.kotlin.webflux.r2dbc.domain.validator.*
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

class ServerRequestTest : StringSpec({
  val mockValidator = mockk<ModelValidator>()
  val mockServerRequest = mockk<ServerRequest>()

  "ServerRequest.bodyToValidatedMono는 modelValidator.validate를 호출한다" {
    val result = "Valid Data"

    every { mockServerRequest.bodyToMono(Any::class.java) } returns Mono.just(result)
    every { mockValidator.validate(result) } returns result

    mockServerRequest.bodyToValidatedMono(Any::class.java, mockValidator).subscribe()

    verify { mockServerRequest.bodyToMono(Any::class.java) }
    verify { mockValidator.validate(result) }
  }

  afterTest {
    clearAllMocks()
  }
})
