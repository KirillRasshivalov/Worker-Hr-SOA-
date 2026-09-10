package itmo.soa.worker.rest;

import itmo.soa.worker.services.ExtraCommandService;
import itmo.soa.workerhr.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/extra")
public class ExtraCommandController {

    private final ExtraCommandService extraCommandService;

    @DeleteMapping("/delete-all-workers-with-person")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllWorkersWithPerson(@RequestBody Person person) {
        log.info("Пришел запрос на удаление всех работников с человеком: {}", person);
        extraCommandService.deleteAllWorkersWithPerson(person);
    }

    @DeleteMapping("/delete-worker-with-salary/{salary}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorkerWithSalary(@PathVariable float salary) {
        log.info("Пришел запрос на удаление работника с зарплатой: {}", salary);
        extraCommandService.deleteWorkerWithSalary(salary);
    }

    @GetMapping("/average-salary-by-organization")
    @ResponseStatus(HttpStatus.OK)
    public double averageSalaryByOrganization() {
        log.info("Пришел запрос на получение средней зарплаты по организациям");
        return extraCommandService.averageSalaryByOrganization();
    }
}
