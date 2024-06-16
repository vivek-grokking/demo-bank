package rabo.demobank.exceptions

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component
@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.allErrors
            .map { error -> error.defaultMessage!! }
            .sorted()
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.joinToString(", ") { it })
    }

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleInputRequestError(ex: AccountNotFoundException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleInputRequestError(ex: UserNotFoundException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.message)
    }

    @ExceptionHandler(InsufficientFundsException::class)
    fun handleInputRequestError(ex: InsufficientFundsException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(InvalidPaymentTypeException::class)
    fun handleInputRequestError(ex: InvalidPaymentTypeException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(UnauthorizedTransactionException::class)
    fun handleInputRequestError(ex: UnauthorizedTransactionException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.message)
    }

}