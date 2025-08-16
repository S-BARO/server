package com.dh.baro.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.validation.ConstraintViolation
import jakarta.validation.Path
import org.springframework.validation.BindingResult

@JsonInclude(Include.NON_NULL)
data class ErrorResponse(
    val message: String,
    val fieldErrors: List<FieldError>? = null,
    val violationErrors: List<ViolationError>? = null,
) {

    companion object {
        fun from(errorMessage: ErrorMessage): ErrorResponse =
            ErrorResponse(message = errorMessage.message)

        fun from(exception: Exception): ErrorResponse =
            ErrorResponse(message = exception.message ?: exception.localizedMessage ?: "Unexpected error")

        fun from(errorMessage: ErrorMessage, bindingResult: BindingResult): ErrorResponse =
            ErrorResponse(
                message = errorMessage.message,
                fieldErrors = FieldError.from(bindingResult),
            )

        fun from(errorMessage: ErrorMessage, violations: Set<ConstraintViolation<*>>): ErrorResponse =
            ErrorResponse(
                message = errorMessage.message,
                violationErrors = ViolationError.from(violations),
            )
    }

    data class FieldError(
        val field: String,
        val rejectedValue: Any?,
        val reason: String?,
    ) {
        companion object {
            fun from(bindingResult: BindingResult): List<FieldError> =
                bindingResult.fieldErrors.map { e ->
                    FieldError(
                        field = e.field,
                        rejectedValue = e.rejectedValue,
                        reason = e.defaultMessage,
                    )
                }
        }
    }

    data class ViolationError(
        val field: String,
        val rejectedValue: Any?,
        val reason: String?,
    ) {
        companion object {
            fun from(violations: Set<ConstraintViolation<*>>): List<ViolationError> =
                violations.map { v ->
                    ViolationError(
                        field = v.propertyPath.leafName(),
                        rejectedValue = v.invalidValue,
                        reason = v.message,
                    )
                }

            private fun Path.leafName(): String =
                this.iterator().asSequence()
                    .mapNotNull { it.name }
                    .lastOrNull()
                    ?: this.toString()
        }
    }
}
