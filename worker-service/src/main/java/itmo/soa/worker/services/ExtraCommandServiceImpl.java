package itmo.soa.worker.services;

import itmo.soa.worker.entity.PersonEntity;
import itmo.soa.worker.entity.WorkerEntity;
import itmo.soa.worker.repository.PersonRepository;
import itmo.soa.worker.repository.WorkerRepository;
import itmo.soa.workerhr.model.Person;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtraCommandServiceImpl implements ExtraCommandService {

    private final WorkerRepository workerRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public void deleteAllWorkersWithPerson(Person person) {
        log.debug("Пришел запрос на сервис на удаление всех работников с человеком: {}", person);
        List<PersonEntity> persons = personRepository.findAllMatching(person);
        if (persons.isEmpty()) {
            return;
        }
        List<Long> personIds = persons.stream().map(PersonEntity::getId).toList();
        List<WorkerEntity> workers = workerRepository.findAllByPersonIdIn(personIds);
        workerRepository.deleteAll(workers);
    }

    @Override
    @Transactional
    public void deleteWorkerWithSalary(float salary) {
        log.debug("Пришел запрос на сервис на удаление работника с зарплатой: {}", salary);
        workerRepository.findFirstBySalary(salary).ifPresent(workerRepository::delete);
    }

    @Override
    @Transactional
    public double averageSalaryByOrganization() {
        log.debug("Пришел запрос на сервис на получение средней зарплаты по организациям");
        List<WorkerEntity> workers = workerRepository.findAll();
        if (workers.isEmpty()) {
            return 0.0;
        }
        double totalSalary = workers.stream().mapToDouble(WorkerEntity::getSalary).sum();
        return totalSalary / workers.size();
    }
}
