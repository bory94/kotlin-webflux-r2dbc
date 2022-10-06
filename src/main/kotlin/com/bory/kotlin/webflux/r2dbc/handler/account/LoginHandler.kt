package com.bory.kotlin.webflux.r2dbc.handler.account

import com.bory.kotlin.webflux.r2dbc.domain.AccountCredentials
import com.bory.kotlin.webflux.r2dbc.domain.validator.ModelValidator
import com.bory.kotlin.webflux.r2dbc.domain.validator.bodyToValidatedMono
import com.bory.kotlin.webflux.r2dbc.exception.UnAuthorizedException
import com.bory.kotlin.webflux.r2dbc.helper.JwtHelper
import com.bory.kotlin.webflux.r2dbc.helper.JwtResponse
import com.bory.kotlin.webflux.r2dbc.helper.RefreshTokenRequest
import com.bory.kotlin.webflux.r2dbc.repository.account.AccountRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class LoginHandler(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtHelper: JwtHelper,
    private val validator: ModelValidator
) {
    private val logger: Logger = LoggerFactory.getLogger(LoginHandler::class.java)

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    suspend fun login(serverRequest: ServerRequest) =
        serverRequest.bodyToValidatedMono(AccountCredentials::class.java, validator).awaitSingle()
            .let { accountCredential ->
                val foundAccount =
                    accountRepository.findAccountWithRolesByEmail(accountCredential.email!!)
                        .awaitFirstOrNull()
                if (foundAccount == null) {
                    logger.debug("Account by email [${accountCredential.email}] not found.")
                    throw UnAuthorizedException()
                }

                if (!passwordEncoder.matches(accountCredential.password, foundAccount.password)) {
                    logger.debug("Password not matched: ${accountCredential.password}")
                    throw UnAuthorizedException()
                }

                val jwtResponse = jwtHelper.createJwtResponse(foundAccount)
                accountRepository.updateRefreshToken(foundAccount.email!!, jwtResponse.refreshToken)
                    .awaitSingle()

                ok().bodyValueAndAwait(jwtResponse)
            }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    suspend fun refreshAccessToken(serverRequest: ServerRequest) =
        serverRequest.bodyToValidatedMono(RefreshTokenRequest::class.java, validator)
            .awaitSingle().refreshToken
            .let { refreshToken ->
                try {
                    val email = jwtHelper.validateToken(refreshToken).body.subject
                    val account = accountRepository.findAccountWithRolesByEmail(email).awaitSingle()
                    if (refreshToken != account.refreshToken) {
                        throw UnAuthorizedException()
                    }

                    val accessToken = jwtHelper.createAccessToken(account)
                    ok().bodyValueAndAwait(JwtResponse(accessToken, refreshToken))
                } catch (e: Exception) {
                    throw UnAuthorizedException()
                }
            }
}
