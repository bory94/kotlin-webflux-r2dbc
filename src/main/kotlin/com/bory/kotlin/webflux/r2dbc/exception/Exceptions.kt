package com.bory.kotlin.webflux.r2dbc.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

sealed class GenericException(status: HttpStatus, reason: String = "") : ResponseStatusException(status, reason)

class ValidationException(override val message: String) : GenericException(HttpStatus.BAD_REQUEST, message)

class ResourceNotFoundException(override val message: String) : GenericException(HttpStatus.NOT_FOUND, message)

class ResourceAlreadyExistsException(override val message: String) : GenericException(HttpStatus.BAD_REQUEST, message)

class ResourceCreationException(override val message: String) : GenericException(HttpStatus.BAD_REQUEST, message)

class ResourceUpdateException(override val message: String) : GenericException(HttpStatus.BAD_REQUEST, message)

class CacheNotFoundException(override val message: String) : GenericException(HttpStatus.INTERNAL_SERVER_ERROR)

class UnAuthorizedException : GenericException(HttpStatus.UNAUTHORIZED, "unauthorized")

class JwtExpiredException(reason: String = "") : GenericException(HttpStatus.UNAUTHORIZED, reason)
