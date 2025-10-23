package by.baraznov.orderservice.util;

import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.util.feign.FeignClientBadRequestException;
import by.baraznov.orderservice.util.feign.FeignClientNotFoundException;
import by.baraznov.orderservice.util.feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleItemAlreadyExistException(ItemAlreadyExist ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(ItemNotFound.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFound ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(OrderNotFound.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFound ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(FeignClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientNotFoundException(FeignClientNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FeignClientBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientBadRequestException(FeignClientBadRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Endpoint not found: " + request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred " +
                ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, status);
    }
    private String getEnumValuesForField(FieldError err) {
        if (err.getField().equals("status")) {
            return Arrays.toString(OrderStatus.values());
        }
        return "unknown enum values";
    }
}
