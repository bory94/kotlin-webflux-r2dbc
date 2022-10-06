package com.bory.kotlin.webflux.r2dbc.handler.blog

import com.bory.kotlin.webflux.r2dbc.domain.Account
import com.bory.kotlin.webflux.r2dbc.domain.Blog
import com.bory.kotlin.webflux.r2dbc.domain.BlogWithAccount
import com.bory.kotlin.webflux.r2dbc.domain.validator.ModelValidator
import com.bory.kotlin.webflux.r2dbc.domain.validator.bodyToValidatedMono
import com.bory.kotlin.webflux.r2dbc.exception.ResourceCreationException
import com.bory.kotlin.webflux.r2dbc.exception.ResourceNotFoundException
import com.bory.kotlin.webflux.r2dbc.exception.ResourceUpdateException
import com.bory.kotlin.webflux.r2dbc.exception.ValidationException
import com.bory.kotlin.webflux.r2dbc.handler.log.LogHandler
import com.bory.kotlin.webflux.r2dbc.helper.CoroutineHelper.Companion.async
import com.bory.kotlin.webflux.r2dbc.helper.CoroutineHelper.Companion.asyncAwait
import com.bory.kotlin.webflux.r2dbc.repository.account.AccountRepository
import com.bory.kotlin.webflux.r2dbc.repository.blog.BlogRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.net.URI

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
class BlogHandler(
    val blogRepository: BlogRepository,
    val accountRepository: AccountRepository,
    val validator: ModelValidator,
    val logHandler: LogHandler
) {
    suspend fun getAll(serverRequest: ServerRequest): ServerResponse =
        asyncAwait { blogRepository.findAllByOrderByIdDesc().asFlow() }
            .map(::blogWithAccount)
            .let { blogs ->
                ok().bodyAndAwait(blogs)
            }

    private suspend fun blogWithAccount(blog: Blog) =
        BlogWithAccount.of(
            blog,
            async {
                accountRepository.findById(blog.createdBy!!).awaitFirstOrDefault(Account.unknown())
                    .withNoPassword()
            }.await(),
            async {
                accountRepository.findById(blog.modifiedBy!!).awaitFirstOrDefault(Account.unknown())
                    .withNoPassword()
            }.await()
        )

    suspend fun getAllWithJoin(serverRequest: ServerRequest): ServerResponse =
        asyncAwait { blogRepository.findAllWithJoin().asFlow() }
            .let { blogs ->
                ok().bodyAndAwait(blogs)
            }

    suspend fun get(serverRequest: ServerRequest): ServerResponse {
        val blogId = serverRequest.pathVariable("id").toLongOrNull()
            ?: throw ValidationException("Invalid Blog-ID - number type Blog-Id is required.")

        return (asyncAwait { blogRepository.findById(blogId).awaitFirstOrNull() }
            ?: throw ResourceNotFoundException("Blog of $blogId not found."))
            .let { blog -> blogWithAccount(blog) }
            .let { blogWithAccount -> ok().bodyValueAndAwait(blogWithAccount) }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    suspend fun insert(serverRequest: ServerRequest): ServerResponse {
        val blog = serverRequest.bodyToValidatedMono(Blog::class.java, validator).awaitSingle()

        val createdBlog = blogRepository.save(blog).awaitFirstOrNull()
            ?: throw ResourceCreationException("Fail to Create Blog $blog")

        return created(URI("/blog/${createdBlog.id}")).bodyValueAndAwait(createdBlog)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val blogId = serverRequest.pathVariable("id").toLongOrNull()
            ?: throw ValidationException("Invalid Blog-ID - number type Blog-Id is required.")

        val blog = serverRequest.bodyToValidatedMono(Blog::class.java, validator).awaitSingle()
        val searchedBlog = blogRepository.findById(blogId).awaitFirstOrNull()
            ?: throw ResourceNotFoundException("Blog of $blogId not found.")

        return (blogRepository.save(searchedBlog.copyFrom(blog)).awaitFirstOrNull()
            ?: throw ResourceUpdateException("Fail to Update Blog $blog"))
            .let {
                logHandler.log("Blog Updated: $it")
                ok().bodyValueAndAwait(it)
            }
    }
}
