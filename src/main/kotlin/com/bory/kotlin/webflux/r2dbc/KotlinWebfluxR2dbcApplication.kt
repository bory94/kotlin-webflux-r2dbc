package com.bory.kotlin.webflux.r2dbc

import com.bory.kotlin.webflux.r2dbc.repository.account.AccountRepository
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
class KotlinWebfluxR2dbcApplication(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) {
  companion object {
    private val LOGGER: Logger = LoggerFactory.getLogger(KotlinWebfluxR2dbcApplication::class.java)
    private const val EMAIL = "admin@bory.com"
  }

  @Bean
  fun updateAdminPassword() = ApplicationRunner {
    runBlocking {
      val account = accountRepository.findByEmail(EMAIL).awaitSingleOrNull()
      if (account != null && account.password!!.startsWith("password")) {
        account.password = passwordEncoder.encode(account.password)
        val savedAccount = accountRepository.save(account).awaitSingleOrNull()
        LOGGER.debug("account password updated: $savedAccount")
      }
    }
  }
}

fun main(args: Array<String>) {
  runApplication<KotlinWebfluxR2dbcApplication>(*args)
}
