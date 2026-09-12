package itmo.soa.hr.exception;

import itmo.soa.workerhr.model.error.BadGatewayError;
import itmo.soa.workerhr.model.error.BadRequestError;
import itmo.soa.workerhr.model.error.ConflictError;
import itmo.soa.workerhr.model.error.InternalServerError;
import itmo.soa.workerhr.model.error.MethodNotAllowedError;
import itmo.soa.workerhr.model.error.NotAcceptableError;
import itmo.soa.workerhr.model.error.NotFoundError;
import itmo.soa.workerhr.model.error.ServiceUnavailableError;
import itmo.soa.workerhr.model.error.UnsupportedMediaTypeError;
import itmo.soa.workerhr.model.error.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
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
                    status.value(), "RESOURCE_NOT_FOUND", message, "Worker", null
            ));
            case CONFLICT -> xml(status, new ConflictError(
                    status.value(), "STATE_CONFLICT", message, message
            ));
            case BAD_REQUEST -> xml(status, new BadRequestError(
                    status.value(), "BAD_REQUEST", message, null, message
            ));
            case UNPROCESSABLE_ENTITY -> xml(status, new ValidationError(
                    status.value(), "VALIDATION_FAILED", message, List.of()
            ));
            default -> xml(status, new InternalServerError(
                    status.value(), "UNEXPECTED_STATUS", message
            ));
        };
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<?> handleWorkerClientError(RestClientResponseException ex) {
        int remoteStatus = ex.getStatusCode().value();
        String body = ex.getResponseBodyAsString();
        if (remoteStatus == HttpStatus.NOT_FOUND.value()) {
            return xml(HttpStatus.NOT_FOUND, new NotFoundError(
                    404, "UPSTREAM_NOT_FOUND", "Объект не найден в worker-service",
                    "Worker", null
            ));
        }
        if (remoteStatus == HttpStatus.CONFLICT.value()) {
            return xml(HttpStatus.CONFLICT, new ConflictError(
                    409, "UPSTREAM_CONFLICT", "Конфликт данных в worker-service", body
            ));
        }
        if (remoteStatus == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
            return xml(HttpStatus.UNPROCESSABLE_ENTITY, new ValidationError(
                    422, "UPSTREAM_VALIDATION_FAILED", "worker-service отклонил данные", List.of()
            ));
        }
        if (remoteStatus >= 400 && remoteStatus < 500) {
            return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                    400, "UPSTREAM_BAD_REQUEST", "worker-service отклонил запрос",
                    null, body
            ));
        }
        return xml(HttpStatus.BAD_GATEWAY, new BadGatewayError(
                502, "UPSTREAM_ERROR", "Ошибка при обращении к worker-service",
                "worker-service", remoteStatus, body
        ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ServiceUnavailableError> handleWorkerUnavailable(ResourceAccessException ex) {
        log.error("worker-service недоступен", ex);
        return xml(HttpStatus.SERVICE_UNAVAILABLE, new ServiceUnavailableError(
                503, "WORKER_SERVICE_UNAVAILABLE", "worker-service временно недоступен",
                "worker-service"
        ));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<BadGatewayError> handleRestClient(RestClientException ex) {
        log.error("Ошибка HTTP-клиента к worker-service", ex);
        return xml(HttpStatus.BAD_GATEWAY, new BadGatewayError(
                502, "UPSTREAM_CLIENT_ERROR", "Ошибка при обращении к worker-service",
                "worker-service", null, ex.getMessage()
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BadRequestError> handleIllegalArgument(IllegalArgumentException ex) {
        return xml(HttpStatus.BAD_REQUEST, new BadRequestError(
                400, "ILLEGAL_ARGUMENT", ex.getMessage(), null, ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerError> handleGeneric(Exception ex) {
        log.error("Необработанная ошибка HR", ex);
        return xml(HttpStatus.INTERNAL_SERVER_ERROR, new InternalServerError(
                500, "INTERNAL_ERROR", "Внутренняя ошибка сервера"
        ));
    }

    private static <T> ResponseEntity<T> xml(HttpStatus status, T body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(body);
    }
}
