package itmo.soa.worker.rest;

import itmo.soa.worker.mapper.PersonMapper;
import itmo.soa.worker.services.ExtraCommandService;
import itmo.soa.workerhr.model.Country;
import itmo.soa.workerhr.model.EyeColor;
import itmo.soa.workerhr.model.HairColor;
import itmo.soa.workerhr.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/extra", produces = MediaType.APPLICATION_XML_VALUE)
public class ExtraCommandController {

    private final ExtraCommandService extraCommandService;
    private final PersonMapper personMapper;

    @DeleteMapping("/delete-all-workers-with-person")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllWorkersWithPerson(
            @RequestParam int height,
            @RequestParam(required = false) EyeColor eyeColor,
            @RequestParam(required = false) HairColor hairColor,
            @RequestParam Country nationality,
            @RequestParam(name = "location.x", required = false) Long locationX,
            @RequestParam(name = "location.y", required = false) Float locationY,
            @RequestParam(name = "location.z", required = false) Integer locationZ,
            @RequestParam(name = "location.name", required = false) String locationName
    ) {
        Person person = personMapper.fromQueryParams(
                height, eyeColor, hairColor, nationality,
                locationX, locationY, locationZ, locationName
        );
        log.info("Пришел запрос на удаление всех работников с человеком: {}", person);
        extraCommandService.deleteAllWorkersWithPerson(person);
    }

    @DeleteMapping("/delete-worker-with-salary")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorkerWithSalary(@RequestParam float salary) {
        log.info("Пришел запрос на удаление работника с зарплатой: {}", salary);
        extraCommandService.deleteWorkerWithSalary(salary);
    }

    @GetMapping("/average-salary")
    @ResponseStatus(HttpStatus.OK)
    public double averageSalary() {
        log.info("Пришел запрос на получение средней зарплаты");
        return extraCommandService.averageSalary();
    }
}
