package itmo.soa.worker.rest;

import itmo.soa.worker.services.WorkerService;
import itmo.soa.workerhr.model.Worker;
import itmo.soa.workerhr.model.WorkerPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/workers", produces = MediaType.APPLICATION_XML_VALUE)
public class WorkerController {

    private static final Set<String> RESERVED_PARAMS = Set.of("page", "size", "sort", "order");

    private final WorkerService workerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WorkerPage getAllWorkers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam Map<String, String> allParams
    ) {
        Map<String, String> filters = new HashMap<>(allParams);
        filters.keySet().removeAll(RESERVED_PARAMS);
        log.info("Пришел запрос на получение работников: filters={}, page={}, size={}, sort={}, order={}",
                filters, page, size, sort, order);
        return workerService.getAllWorkers(filters, page, size, sort, order);
    }

    @GetMapping("/{worker-id}")
    @ResponseStatus(HttpStatus.OK)
    public Worker getWorkerById(@PathVariable("worker-id") long id) {
        log.info("Пришел запрос на получение работника с id = {}", id);
        return workerService.getWorkerById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Worker addNewWorker(@Valid @RequestBody Worker worker) {
        log.info("Пришел запрос на создание работника");
        return workerService.addNewWorker(worker);
    }

    @PutMapping(value = "/{worker-id}", consumes = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Worker updateWorker(@PathVariable("worker-id") long id, @Valid @RequestBody Worker worker) {
        log.info("Пришел запрос на обновление работника с id = {}", id);
        return workerService.updateWorker(id, worker);
    }

    @DeleteMapping("/{worker-id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorker(@PathVariable("worker-id") long id) {
        log.info("Пришел запрос на удаление работника с id = {}", id);
        workerService.deleteWorker(id);
    }
}
