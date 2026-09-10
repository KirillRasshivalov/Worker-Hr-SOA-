package itmo.soa.worker.services;

import itmo.soa.worker.entity.WorkerEntity;
import itmo.soa.worker.mapper.WorkerMapper;
import itmo.soa.worker.repository.WorkerRepository;
import itmo.soa.worker.specification.WorkerSpecifications;
import itmo.soa.workerhr.model.Person;
import itmo.soa.workerhr.model.Worker;
import itmo.soa.workerhr.model.WorkerPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerServiceImpl implements WorkerService {

    private static final int MAX_PAGE_SIZE = 100;

    private final WorkerMapper workerMapper;
    private final WorkerRepository workerRepository;

    @Override
    @Transactional
    public void addNewWorker(Worker worker) {
        log.debug("Пришел запрос на сервис на создание работника с id = {}", worker.getId());
        WorkerEntity workerEntity = workerMapper.toEntity(worker);
        workerRepository.save(workerEntity);
    }

    @Override
    public Worker getWorkerById(long id) {
        log.debug("Пришел запрос на сервис на получение работника с id = {}", id);
        WorkerEntity workerEntity = workerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Работник с id " + id + " не найден"));
        return workerMapper.toDto(workerEntity);
    }

    @Override
    @Transactional
    public Worker updateWorker(long id, Worker worker) {
        log.debug("Пришел запрос на сервис на обновление работника с id = {}", id);
        WorkerEntity workerEntity = workerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Работник с id " + id + " не найден"));
        workerMapper.updateEntity(workerEntity, worker);
        workerRepository.save(workerEntity);
        return workerMapper.toDto(workerEntity);
    }

    @Override
    @Transactional
    public void deleteWorker(long id) {
        log.debug("Пришел запрос на сервис на удаление работника с id = {}", id);
        WorkerEntity workerEntity = workerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Работник с id " + id + " не найден"));
        workerRepository.delete(workerEntity);
    }

    @Override
    @Transactional
    public WorkerPage getAllWorkers(Map<String, String> filters, int page, int size, String sort, String order) {
        log.debug("Пришел запрос на сервис на получение работников: filters={}, page={}, size={}, sort={}, order={}",
                filters, page, size, sort, order);
        if (page < 0) {
            throw new IllegalArgumentException("page должен быть >= 0");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size должен быть в диапазоне 1.." + MAX_PAGE_SIZE);
        }
        Specification<WorkerEntity> spec = WorkerSpecifications.fromFilters(filters);
        Pageable pageable = PageRequest.of(page, size, buildSort(sort, order));
        Page<WorkerEntity> result = workerRepository.findAll(spec, pageable);

        WorkerPage workerPage = new WorkerPage();
        workerPage.setPage(result.getNumber());
        workerPage.setSize(result.getSize());
        workerPage.setTotalElements(result.getTotalElements());
        workerPage.setTotalPages(result.getTotalPages());
        workerPage.setItems(result.getContent().stream().map(workerMapper::toDto).toList());
        return workerPage;
    }

    private static Sort buildSort(String sort, String order) {
        String sortField = (sort == null || sort.isBlank()) ? "id" : sort.trim();
        if (!WorkerSpecifications.ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Недопустимое поле сортировки: " + sortField);
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (order != null && !order.isBlank()) {
            direction = switch (order.trim().toLowerCase()) {
                case "asc" -> Sort.Direction.ASC;
                case "desc" -> Sort.Direction.DESC;
                default -> throw new IllegalArgumentException("order должен быть asc или desc");
            };
        }
        return Sort.by(direction, sortField);
    }
}
