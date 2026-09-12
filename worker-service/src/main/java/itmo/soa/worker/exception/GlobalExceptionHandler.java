package itmo.soa.worker.exception;

import itmo.soa.workerhr.model.error.BadRequestError;
import itmo.soa.workerhr.model.error.ConflictError;
import itmo.soa.workerhr.model.error.FieldViolation;
import itmo.soa.workerhr.model.error.InternalServerError;
import itmo.soa.workerhr.model.error.MethodNotAllowedError;
import itmo.soa.workerhr.model.error.NotAcceptableError;
import itmo.soa.workerhr.model.error.NotFoundError;
import itmo.soa.workerhr.model.error.ServiceUnavailableError;
import itmo.soa.workerhr.model.error.UnsupportedMediaTypeError;
import itmo.soa.workerhr.model.error.ValidationError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return switch (status) {
            case NOT_FOUND -> xml(status, new NotFoundError(
                    status.value(), "RESOURCE_NOT_FOUND", message,
                    guessResource(message), guessResourceId(message)
            ));
            case CONFLICT -> xml(status, new ConflictError(
                    status.value(), "STATE_CONFLICT", message, message
            ));
            case UNPROCESSABLE_ENTITY -> xml(status, new ValidationError(
                    status.value(), "VALIDATION_FAILED", message, List.of()
            ));
            case BAD_REQUEST -> xml(status, new BadRequestError(
                    status.value(), "BAD_REQUEST", message, null, message
            ));
            default -> xml(status, new InternalServerError(
                    status.value(), "UNEXPECTED_STATUS", message
            ));
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toViolation)
                .toList();
        return xml(HttpStatus.UNPROCESSABLE_ENTITY, new ValidationError(
                422, "BODY_VALIDATION_FAILED", "Ошибка валидации тела запроса", violations
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> handleConstraintViolation(ConstraintViolationException ex) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(v -> new FieldViolation(String.valueOf(v.getPropertyPath()), v.getMessage()))
                .toList();
        return xml(HttpStatus.UNPROCESSABLE_ENTITY, new ValidationError(
                422, "CONSTRAINT_VIOLATION", "Ошибка валидации", violations
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestError> handleUnreadable(HttpMessageNotReadableException ex) {
        return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                400, "MALFORMED_BODY", "Некорректное или повреждённое тело запроса",
                "body", rootMessage(ex)
        ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<UnsupportedMediaTypeError> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex
    ) {
        String supported = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));
        String contentType = ex.getContentType() != null ? ex.getContentType().toString() : null;
        return xml(HttpStatus.UNSUPPORTED_MEDIA_TYPE, new UnsupportedMediaTypeError(
                415, "UNSUPPORTED_MEDIA_TYPE", "Неподдерживаемый Content-Type",
                contentType, supported.isBlank() ? "application/xml" : supported
        ));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<NotAcceptableError> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return xml(HttpStatus.NOT_ACCEPTABLE, new NotAcceptableError(
                406, "NOT_ACCEPTABLE", "Неподдерживаемый Accept",
                null, "application/xml"
        ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MethodNotAllowedError> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {
        String supported = ex.getSupportedMethods() == null
                ? null
                : Arrays.stream(ex.getSupportedMethods()).collect(Collectors.joining(", "));
        return xml(HttpStatus.METHOD_NOT_ALLOWED, new MethodNotAllowedError(
                405, "METHOD_NOT_ALLOWED", "Метод не поддерживается",
                ex.getMethod(), supported
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BadRequestError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                400, "TYPE_MISMATCH", "Некорректный параметр запроса",
                ex.getName(), "Параметр '" + ex.getName() + "' имеет некорректный тип"
        ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BadRequestError> handleMissingParam(MissingServletRequestParameterException ex) {
        return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                400, "MISSING_PARAMETER", "Отсутствует обязательный параметр",
                ex.getParameterName(), "Параметр обязателен"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BadRequestError> handleIllegalArgument(IllegalArgumentException ex) {
        return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                400, "ILLEGAL_ARGUMENT", ex.getMessage(), null, ex.getMessage()
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ConflictError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return xml(HttpStatus.CONFLICT, new ConflictError(
                409, "DATA_INTEGRITY_VIOLATION", "Конфликт целостности данных",
                rootMessage(ex)
        ));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ServiceUnavailableError> handleDbUnavailable(DataAccessResourceFailureException ex) {
        log.error("БД недоступна", ex);
        return xml(HttpStatus.SERVICE_UNAVAILABLE, new ServiceUnavailableError(
                503, "DATABASE_UNAVAILABLE", "Сервис временно недоступен (БД)", "database"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerError> handleGeneric(Exception ex) {
        log.error("Необработанная ошибка", ex);
        return xml(HttpStatus.INTERNAL_SERVER_ERROR, new InternalServerError(
                500, "INTERNAL_ERROR", "Внутренняя ошибка сервера"
        ));
    }

    private FieldViolation toViolation(FieldError error) {
        return new FieldViolation(error.getField(), error.getDefaultMessage());
    }

    private static String guessResource(String message) {
        if (message == null) {
            return "unknown";
        }
        String lower = message.toLowerCase();
        if (lower.contains("работник") || lower.contains("worker")) {
            return "Worker";
        }
        if (lower.contains("организац") || lower.contains("organization")) {
            return "Organization";
        }
        if (lower.contains("person")) {
            return "Person";
        }
        return "Resource";
    }

    private static String guessResourceId(String message) {
        if (message == null) {
            return null;
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\bid\\s*=?\\s*(\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        matcher = java.util.regex.Pattern.compile("salary\\s*=\\s*([0-9.]+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    private static <T> ResponseEntity<T> xml(HttpStatus status, T body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(body);
    }
}
