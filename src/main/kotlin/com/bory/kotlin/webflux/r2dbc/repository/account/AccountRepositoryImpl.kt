package com.bory.kotlin.webflux.r2dbc.repository.account

import com.bory.kotlin.webflux.r2dbc.domain.Account
import com.bory.kotlin.webflux.r2dbc.domain.AccountRole
import com.bory.kotlin.webflux.r2dbc.domain.AccountWithRoles
import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Update.update
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AccountRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val databaseTemplate: R2dbcEntityTemplate
) : AccountRepositoryCustom {
    companion object {
        private const val SQL_ACCOUNT_WITH_ROLE_BY_EMAIL = """
      SELECT A.id as id, A.email as email, A.name as accountName, A.password as password,
             A.refresh_token, A.created_at, A.modified_at,
             C.id as roleId, C.name as roleName
        FROM account A
       INNER JOIN account_role_mapping B ON B.account_id = A.id 
       INNER JOIN account_role C ON C.id = B.role_id
       WHERE A.email = :email
       ORDER BY A.id
    """

        /**
         * disclaimer:
         * r2dbc-mysql driver 0.8.2.RELEASE has a bug with fetch() method
         * use explicit mapping method instead of fetch()
         * this will be fixed r2dbc-mysql version 0.8.3.RELEASE
         *
         * https://github.com/mirromutth/r2dbc-mysql/issues/149
         * https://github.com/mirromutth/r2dbc-mysql/pull/159
         */
        private fun mapRowToMap(row: Row) = mapOf(
            "id" to row["id"],
            "email" to row["email"],
            "password" to row["password"],
            "accountName" to row["accountName"],
            "refresh_token" to (row["refresh_token"] ?: ""),
            "created_at" to row["created_at"],
            "modified_at" to row["modified_at"],
            "roleId" to row["roleId"],
            "roleName" to row["roleName"]
        )
    }

    override fun findAccountWithRolesByEmail(email: String) =
        databaseClient.sql(SQL_ACCOUNT_WITH_ROLE_BY_EMAIL)
            .bind("email", email)
            .map(AccountRepositoryImpl::mapRowToMap)
            .all()
            .bufferUntilChanged { map -> map["id"] }
            .map { accountRolesMapList ->
                AccountWithRoles.fromMap(accountRolesMapList[0]).apply {
                    accountRolesMapList.map { map ->
                        AccountRole(id = map["roleId"] as Long, name = map["roleName"] as String)
                    }.forEach(this::addRole)
                }
            }
            .single()

    override fun updateRefreshToken(email: String, refreshToken: String) =
        databaseTemplate.update(Account::class.java).inTable("account")
            .matching(query(where("email").`is`(email)))
            .apply(
                update("refresh_token", refreshToken)
                    .set("modified_at", LocalDateTime.now())
            )
}
