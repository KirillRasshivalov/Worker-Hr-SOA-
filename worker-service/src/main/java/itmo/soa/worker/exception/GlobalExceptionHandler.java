package itmo.soa.worker.exception;

import itmo.soa.workerhr.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return xmlError(status, message, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return xmlError(HttpStatus.BAD_REQUEST, "Ошибка валидации", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return xmlError(HttpStatus.BAD_REQUEST, "Ошибка валидации", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return xmlError(HttpStatus.BAD_REQUEST, "Некорректное тело запроса", rootMessage(ex));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String details = "Параметр '" + ex.getName() + "' имеет некорректный тип";
        return xmlError(HttpStatus.BAD_REQUEST, "Некорректный параметр запроса", details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return xmlError(HttpStatus.BAD_REQUEST, "Отсутствует обязательный параметр", ex.getParameterName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return xmlError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Необработанная ошибка", ex);
        return xmlError(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", null);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private static String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    private static ResponseEntity<ErrorResponse> xmlError(HttpStatus status, String message, String details) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse(status.value(), message, details));
    }
}
