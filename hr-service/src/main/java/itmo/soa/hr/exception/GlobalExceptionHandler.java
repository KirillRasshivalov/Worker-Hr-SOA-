package itmo.soa.hr.exception;

import itmo.soa.workerhr.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return xmlError(status, message, null);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWorkerClientError(RestClientResponseException ex) {
        int remoteStatus = ex.getStatusCode().value();
        if (remoteStatus == HttpStatus.NOT_FOUND.value()) {
            return xmlError(HttpStatus.NOT_FOUND, "Объект не найден в worker-service", ex.getResponseBodyAsString());
        }
        if (remoteStatus >= 400 && remoteStatus < 500) {
            return xmlError(HttpStatus.BAD_REQUEST, "worker-service отклонил запрос", ex.getResponseBodyAsString());
        }
        return xmlError(HttpStatus.BAD_GATEWAY, "Ошибка при обращении к worker-service", ex.getResponseBodyAsString());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return xmlError(HttpStatus.BAD_REQUEST, "Некорректный параметр запроса", ex.getName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Необработанная ошибка HR", ex);
        return xmlError(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", null);
    }

    private static ResponseEntity<ErrorResponse> xmlError(HttpStatus status, String message, String details) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse(status.value(), message, details));
    }
}
