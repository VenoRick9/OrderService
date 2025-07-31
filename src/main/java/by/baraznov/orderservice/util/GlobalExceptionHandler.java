package by.baraznov.orderservice.util;

import by.baraznov.orderservice.model.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

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


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    if (err.getCode() != null && err.getCode().equals("EnumValidator")) {
                        return err.getField() + ": must be one of " + getEnumValuesForField(err);
                    }
                    return err.getField() + ": " + err.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String allowedValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            String message = String.format("Invalid value '%s'. Allowed values: %s", ex.getValue(), allowedValues);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
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
