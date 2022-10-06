package com.bory.kotlin.webflux.r2dbc.domain

import io.jsonwebtoken.*
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import java.time.LocalDateTime
import javax.validation.constraints.*

@Table("account")
data class Account(
    @Id
    var id: Long? = null,
    @field:NotEmpty(message = "account.email.empty")
    @field:Size(min = 5, max = 100, message = "account.email.size")
    @field:Email(message = "account.email.invalid")
    var email: String?,
    @field:NotEmpty(message = "account.password.empty")
    @field:Size(min = 5, max = 36, message = "account.password.size")
    var password: String?,
    @field:NotEmpty(message = "account.name.empty")
    @field:Size(min = 3, max = 100, message = "account.name.size")
    var name: String?,
    var refreshToken: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var modifiedAt: LocalDateTime = LocalDateTime.now()
) {
  fun copyFrom(source: Account): Account {
    id = source.id
    email = source.email
    name = source.name
    password = source.password
    createdAt = source.createdAt
    modifiedAt = source.modifiedAt

    return this
  }

  fun withNoPassword() = unknown().copyFrom(this).apply { password = null }

  companion object {
    fun unknown() = Account(-1, "unknown@unknown.com", "", "UNKNOWN")

    fun fromAccountWithRoles(accountWithRoles: AccountWithRoles) =
        Account(
            id = accountWithRoles.id,
            email = accountWithRoles.email,
            name = accountWithRoles.name,
            createdAt = accountWithRoles.createdAt,
            modifiedAt = accountWithRoles.modifiedAt,
            password = null
        )

    suspend fun currentContext(): Account =
        ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as Account
  }
}

data class AccountCredentials(
    @NotEmpty(message = "account.email.empty")
    @Email(message = "account.email.invalid")
    var email: String?,
    @NotEmpty(message = "account.password.empty")
    var password: String?
)

data class AccountWithBlogCount(
    var id: Long?,
    var email: String?,
    var password: String?,
    var name: String?,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var blogCount: Long
) {
  companion object {
    fun of(account: Account, blogCount: Long) =
        AccountWithBlogCount(account.id, account.email, account.password, account.name, account.createdAt, account.modifiedAt, blogCount)
  }
}

data class AccountWithBlogs(
    var id: Long?,
    var email: String?,
    var password: String?,
    var name: String?,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var blogs: List<Blog>
) {
  companion object {
    fun of(account: Account, blogs: List<Blog>) =
        AccountWithBlogs(account.id, account.email, account.password, account.name, account.createdAt, account.modifiedAt, blogs)

  }
}

@Table("account_role")
data class AccountRole(
    @Id
    var id: Long?,
    @field:NotEmpty(message = "account.role.empty")
    @field:Size(min = 5, max = 100, message = "account.role.size")
    var name: String?,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var modifiedAt: LocalDateTime = LocalDateTime.now()
)

data class AccountWithRoles(
    var id: Long?,
    var email: String?,
    var password: String?,
    var name: String?,
    var refreshToken: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var modifiedAt: LocalDateTime = LocalDateTime.now(),
    val roles: MutableSet<AccountRole> = mutableSetOf()
) {
  fun addRole(role: AccountRole) {
    roles.add(role)
  }

  companion object {
    fun fromJwsClaims(jws: Jws<Claims>): AccountWithRoles {
      val roles = jws.body["roles", String::class.java]
          .split(",")
          .filter { it.trim().isNotEmpty() }
          .map { AccountRole(id = null, name = it) }
          .toMutableSet()

      return AccountWithRoles(
          id = jws.body["id", Integer::class.java].toLong(),
          email = jws.body["email", String::class.java],
          name = jws.body["name", String::class.java],
          password = null,
          roles = roles
      )
    }

    fun fromMap(map: Map<String, Any?>) =
        AccountWithRoles(
            id = map["id"] as Long,
            email = map["email"] as String,
            password = map["password"] as String,
            name = map["accountName"] as String,
            refreshToken = map["refresh_token"] as String,
            createdAt = map["created_at"] as LocalDateTime,
            modifiedAt = map["modified_at"] as LocalDateTime
        )
  }
}
