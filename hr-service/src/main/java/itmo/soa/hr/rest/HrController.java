package itmo.soa.hr.rest;

import itmo.soa.hr.service.HrService;
import itmo.soa.workerhr.model.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/hr", produces = MediaType.APPLICATION_XML_VALUE)
public class HrController {

    private final HrService hrService;

    @PostMapping("/fire/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Worker fireWorker(@PathVariable long id) {
        log.info("Пришел запрос на увольнение работника с id = {}", id);
        return hrService.fire(id);
    }

    @PostMapping("/move/{worker-id}/{id-from}/{id-to}")
    @ResponseStatus(HttpStatus.OK)
    public Worker moveWorker(
            @PathVariable("worker-id") long workerId,
            @PathVariable("id-from") long idFrom,
            @PathVariable("id-to") long idTo
    ) {
        log.info("Пришел запрос на перемещение работника с id = {} из организации {} в организацию {}",
                workerId, idFrom, idTo);
        return hrService.move(workerId, idFrom, idTo);
    }
}
