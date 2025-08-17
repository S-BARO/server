package com.dh.baro.core.advice

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.ErrorResponse
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.core.exception.ForbiddenException
import com.dh.baro.core.exception.UnauthorizedException
import jakarta.validation.ConstraintViolationException
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    // Body 검증
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.FIELD_ERROR, e.bindingResult)
    }

    // ModelAttribute 바인딩/검증
    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(e: BindException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.FIELD_ERROR, e.bindingResult)
    }

    // Query/Path 파라미터 검증
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(e: ConstraintViolationException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.URL_PARAMETER_ERROR, e.constraintViolations)
    }

    // 필수 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameter(e: MissingServletRequestParameterException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.URL_PARAMETER_ERROR)
    }

    // 필수 헤더 누락
    @ExceptionHandler(MissingRequestHeaderException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingRequestHeader(e: MissingRequestHeaderException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.MISSING_REQUEST_HEADER)
    }

    // 파라미터 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.METHOD_ARGUMENT_TYPE_MISMATCH)
    }

    // 클라이언트 전송 중단
    @ExceptionHandler(ClientAbortException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleClientAbort(e: ClientAbortException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.ALREADY_DISCONNECTED)
    }

    // JSON 파싱 오류 등
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(ErrorMessage.INVALID_JSON)
    }

    // 비즈니스 검증 실패
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(e: IllegalArgumentException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse.from(e)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(e: UnauthorizedException): ErrorResponse {
        logger.warn("[Unauthorized] : ${e.message}", e)
        return ErrorResponse.from(e)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(e: ForbiddenException): ErrorResponse {
        logger.warn("[Forbidden] : ${e.message}", e)
        return ErrorResponse.from(e)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFound(e: NoResourceFoundException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse(ErrorMessage.NO_RESOURCE_FOUND.message)
    }

    // 지원하지 않는 메서드 호출
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotSupported(e: HttpRequestMethodNotSupportedException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse(ErrorMessage.METHOD_NOT_SUPPORTED.message)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleMethodNotSupported(e: ConflictException): ErrorResponse {
        logger.warn(e.message)
        return ErrorResponse(ErrorMessage.METHOD_NOT_SUPPORTED.message)
    }

    // 서버가 지원하지 않는 미디어 타입
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleMediaTypeNotSupported(e: HttpMediaTypeNotSupportedException): ErrorResponse {
        logger.info(e.message)
        return ErrorResponse(ErrorMessage.MEDIA_TYPE_NOT_SUPPORTED.message)
    }

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalState(e: IllegalStateException): ErrorResponse {
        logger.error(e.message, e)
        return ErrorResponse.from(e)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnhandled(e: Exception): ErrorResponse {
        logger.error(e.message, e)
        return ErrorResponse.from(ErrorMessage.UNHANDLED_EXCEPTION)
    }
}
