package itmo.soa.worker.services;

import itmo.soa.worker.entity.WorkerEntity;
import itmo.soa.worker.repository.WorkerRepository;
import itmo.soa.worker.specification.WorkerSpecifications;
import itmo.soa.workerhr.model.Person;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtraCommandServiceImpl implements ExtraCommandService {

    private final WorkerRepository workerRepository;

    @Override
    @Transactional
    public void deleteAllWorkersWithPerson(Person person) {
        log.debug("Пришел запрос на сервис на удаление всех работников с человеком: {}", person);
        if (person == null || person.getHeight() == null || person.getNationality() == null) {
            throw new IllegalArgumentException("Для удаления по person обязательны height и nationality");
        }
        if (person.getHeight() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "height должен быть > 0");
        }
        List<WorkerEntity> workers = workerRepository.findAll(WorkerSpecifications.personEquivalent(person));
        if (workers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Работники с указанным person не найдены");
        }
        workerRepository.deleteAll(workers);
    }

    @Override
    @Transactional
    public void deleteWorkerWithSalary(float salary) {
        log.debug("Пришел запрос на сервис на удаление работника с зарплатой: {}", salary);
        if (salary <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary должен быть > 0");
        }
        WorkerEntity worker = workerRepository.findFirstBySalary(salary)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Работник с salary=" + salary + " не найден"
                ));
        workerRepository.delete(worker);
    }

    @Override
    @Transactional
    public double averageSalary() {
        log.debug("Пришел запрос на сервис на получение средней зарплаты");
        List<WorkerEntity> workers = workerRepository.findAll();
        if (workers.isEmpty()) {
            return 0.0;
        }
        return workers.stream().mapToDouble(WorkerEntity::getSalary).sum() / workers.size();
    }
}
