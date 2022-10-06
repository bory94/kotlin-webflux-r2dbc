package com.bory.kotlin.webflux.r2dbc.domain.validator

import com.bory.kotlin.webflux.r2dbc.exception.ValidationException
import org.springframework.context.MessageSource
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import java.util.*

@Component
class ModelValidator(
    private val messageSource: MessageSource,
    private val validator: Validator
) : Validator {

    override fun supports(clazz: Class<*>) = true

    override fun validate(target: Any, errors: Errors) {
        validator.validate(target, errors)
    }

    fun <T> validate(target: T): T {
        val errors = BeanPropertyBindingResult(target, Any::class.qualifiedName!!)
        validate(target as Any, errors)

        return if (errors.allErrors.isEmpty()) target
        else {
            errors.allErrors.joinToString(separator = "\n") {
                messageSource.getMessage(
                    DefaultMessageSourceResolvable(
                        arrayOf(it.defaultMessage),
                        it.arguments ?: arrayOf(),
                        it.defaultMessage ?: "UNKNOWN ERROR"
                    ),
                    Locale.KOREAN
                )
            }.let { throw ValidationException(it) }
        }
    }
}

fun <T> ServerRequest.bodyToValidatedMono(clazz: Class<out T>, validator: ModelValidator): Mono<T> =
    this.bodyToMono(clazz).map { validator.validate(it) }
