package com.bory.kotlin.webflux.r2dbc.validator

import com.bory.kotlin.webflux.r2dbc.domain.Account
import com.bory.kotlin.webflux.r2dbc.domain.validator.ModelValidator
import com.bory.kotlin.webflux.r2dbc.exception.ValidationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.slf4j.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class ModelValidatorTest(private val modelValidator: ModelValidator) : StringSpec({
  val logger: Logger = LoggerFactory.getLogger(ModelValidatorTest::class.java)

  beforeSpec { logger.debug("Starting spec... $it") }
  beforeTest { logger.debug("A Test is now being starting... $it") }
  afterTest { logger.debug("A Test is now being shutting down... $it") }
  afterSpec { logger.debug("Shutting down spec... $it") }

  "모든 정보가 정상인 Account를 검증하면 입력한 Account가 그대로 리턴된다" {
    val account = Account(name = "Valid Name", email = "valid@email.com", password = "12345678")

    modelValidator.validate(account) shouldBe account
  }

  "이름이 null인 Account를 검증하면 ValidationException이 throw된다" {
    val account = Account(name = null, email = "valid@email.com", password = "12345678")
    val exception = shouldThrow<ValidationException> { modelValidator.validate(account) }

    exception.message shouldBe "이름이 입력되지 않았습니다."
  }

  "이메일 형식이 맞지 않는 Account를 검증하면 ValidationException이 throw된다" {
    val account = Account(name = "name", email = "InvalidEmail@", password = "1235135")
    val exception = shouldThrow<ValidationException> { modelValidator.validate(account) }

    exception.message shouldBe "올바르지 않은 email 형식입니다."
  }

  "비밀번호가 짧으면 ValidationException이 throw된다" {
    val account = Account(name = "name", email = "valid@email.com", password = "123")
    val exception = shouldThrow<ValidationException> { modelValidator.validate(account) }

    exception.message shouldBe "비밀번호 길이가 올바르지 않습니다. 최소 5, 최대 36 글자여야 합니다."
  }

  "supports 메소드는 항상 true를 리턴한다" {
    modelValidator.supports(Any::class.java) shouldBe true
  }
})
