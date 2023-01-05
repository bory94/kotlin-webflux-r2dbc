package com.bory.kotlin.webflux.r2dbc.handler.account

import com.bory.kotlin.webflux.r2dbc.domain.Account
import com.bory.kotlin.webflux.r2dbc.domain.AccountWithBlogCount
import com.bory.kotlin.webflux.r2dbc.domain.AccountWithBlogs
import com.bory.kotlin.webflux.r2dbc.domain.validator.ModelValidator
import com.bory.kotlin.webflux.r2dbc.domain.validator.bodyToValidatedMono
import com.bory.kotlin.webflux.r2dbc.exception.*
import com.bory.kotlin.webflux.r2dbc.handler.log.LogHandler
import com.bory.kotlin.webflux.r2dbc.helper.CoroutineHelper.Companion.async
import com.bory.kotlin.webflux.r2dbc.helper.CoroutineHelper.Companion.asyncAwait
import com.bory.kotlin.webflux.r2dbc.repository.account.AccountRepository
import com.bory.kotlin.webflux.r2dbc.repository.blog.BlogRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.net.URI

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
class AccountHandler(
    private val accountRepository: AccountRepository,
    private val blogRepository: BlogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val validator: ModelValidator,
    private val logHandler: LogHandler
) {
    /**
     * Get all accounts with count
     *
     * @param serverRequest
     */
    suspend fun getAllWithCount(serverRequest: ServerRequest) =
        accountRepository.findAll().asFlow()
            .map { account ->
                val count =
                    asyncAwait {
                        blogRepository.countByCreatedBy(account.id!!).awaitFirstOrDefault(0)
                    }
                AccountWithBlogCount.of(account.withNoPassword(), count)
            }.let { accountFlow ->
                ok().bodyAndAwait(accountFlow)
            }

    /**
     * Get an account with all blogs it writes
     *
     * @param serverRequest
     * @return
     */
    suspend fun get(serverRequest: ServerRequest): ServerResponse {
        val accountId = serverRequest.pathVariable("id").toLongOrNull()
            ?: throw ValidationException("Invalid Account-ID - number type Account-Id is required.")

        val account = asyncAwait { accountRepository.findById(accountId).awaitFirstOrNull() }
            ?: throw ResourceNotFoundException("Account-ID of $accountId not found.")

        val blogs = async { blogRepository.findByCreatedBy(accountId).asFlow() }

        return AccountWithBlogs.of(account.withNoPassword(), blogs.await().toList())
            .let { accountWithBlogs ->
                ok().bodyValueAndAwait(accountWithBlogs)
            }
    }

    suspend fun current(serverRequest: ServerRequest) =
        Account.currentContext()
            .let { ok().bodyValueAndAwait(it) }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    suspend fun insert(serverRequest: ServerRequest): ServerResponse {
        val account =
            serverRequest.bodyToValidatedMono(Account::class.java, validator).awaitSingle()

        if (accountRepository.existsByEmail(account.email!!).awaitSingle()) {
            throw ResourceAlreadyExistsException("Email [${account.email}] already exists.")
        }

        account.password = passwordEncoder.encode(account.password)
        val createdAccount = accountRepository.save(account).awaitSingleOrNull()
            ?: throw ResourceCreationException("Fail to Create Account $account")

        logHandler.log("New Account Created: $createdAccount")

        return created(URI("/account/${createdAccount.id}")).bodyValueAndAwait(createdAccount.withNoPassword())
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val accountId = serverRequest.pathVariable("id").toLongOrNull()
            ?: throw ValidationException("Invalid Account-ID - number type Account-Id is required.")

        val account =
            serverRequest.bodyToValidatedMono(Account::class.java, validator).awaitSingle()
        account.id = account.id ?: accountId
        account.password = passwordEncoder.encode(account.password)

        if (accountRepository.existsByEmailAndIdNot(account.email!!, account.id!!).awaitSingle()) {
            throw ResourceAlreadyExistsException("Email [${account.email}] already exists.")
        }

        val searchedAccount = accountRepository.findById(accountId).awaitSingleOrNull()
            ?: throw ResourceNotFoundException("Account of $accountId not found.")

        if (searchedAccount.email != account.email) {
            throw ValidationException("Email Not Matched.")
        }

        return (accountRepository.save(searchedAccount.copyFrom(account)).awaitSingleOrNull()
            ?: throw ResourceUpdateException("Fail to Update Account $account"))
            .let {
                logHandler.log("Account Updated: $it")
                ok().bodyValueAndAwait(it.withNoPassword())
            }
    }
}
