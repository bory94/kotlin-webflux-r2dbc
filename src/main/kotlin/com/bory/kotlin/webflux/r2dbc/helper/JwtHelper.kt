package com.bory.kotlin.webflux.r2dbc.helper

import com.bory.kotlin.webflux.r2dbc.domain.AccountWithRoles
import com.bory.kotlin.webflux.r2dbc.exception.JwtExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.validation.constraints.NotEmpty

@Component
class JwtHelper(
    @Value("\${security.jwt.secret}")
    private val secret: String,

    @Value("\${security.jwt.access-token-timeout:30}")
    private val accessTokenTimeout: String,

    @Value("\${security.jwt.refresh-token-timeout:30}")
    private val refreshTokenTimeout: String,
) {
    private var key: Key? = null

    fun createAccessToken(account: AccountWithRoles): String =
        account.let {
            Jwts.builder()
                .signWith(this.key)
                .setSubject(it.email)
                .setIssuer("identity")
                .addClaims(accountToMap(it))
                .setExpiration(
                    Date.from(
                        Instant.now().plus(Duration.ofMinutes(this.accessTokenTimeout.toLong()))
                    )
                )
                .setIssuedAt(Date.from(Instant.now()))
                .compact()
        }

    fun createRefreshToken(account: AccountWithRoles): String =
        account.let {
            Jwts.builder()
                .signWith(this.key)
                .setSubject(it.email)
                .setIssuer("identity")
                .setExpiration(
                    Date.from(
                        Instant.now().plus(Duration.ofDays(this.refreshTokenTimeout.toLong()))
                    )
                )
                .setIssuedAt(Date.from(Instant.now()))
                .compact()
        }

    fun validateToken(token: String): Jws<Claims> {
        return try {
            Jwts.parserBuilder().setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
        } catch (expire: ExpiredJwtException) {
            throw JwtExpiredException("JWT has been expired.")
        } catch (e: Exception) {
            throw BadCredentialsException("Could not validate jwt.")
        }
    }

    fun createJwtResponse(account: AccountWithRoles) =
        JwtResponse(createAccessToken(account), createRefreshToken(account))

    private fun accountToMap(accountWithRoles: AccountWithRoles): Map<String, Any> =
        mapOf(
            "id" to accountWithRoles.id!!,
            "email" to accountWithRoles.email!!,
            "name" to accountWithRoles.name!!,
            "roles" to accountWithRoles.roles.joinToString(",") { it.name!! }
        )

    @PostConstruct
    fun initializeKey() {
        this.key = Keys.hmacShaKeyFor(this.secret.toByteArray())
    }
}

data class RefreshTokenRequest(
    @NotEmpty
    var refreshToken: String
)

data class JwtResponse(
    var accessToken: String,
    var refreshToken: String,
    var createdDate: LocalDateTime = LocalDateTime.now()
)
