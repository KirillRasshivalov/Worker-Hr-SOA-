package itmo.soa.hr.service;

import itmo.soa.hr.client.WorkerApiClient;
import itmo.soa.workerhr.model.Status;
import itmo.soa.workerhr.model.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrServiceImpl implements HrService {

    private final WorkerApiClient workerApiClient;

    @Override
    public Worker fire(long workerId) {
        log.debug("HR fire workerId={}", workerId);
        Worker worker = workerApiClient.getById(workerId);
        worker.setStatus(Status.FIRED);
        worker.setOrganizationId(null);
        return workerApiClient.update(workerId, worker);
    }

    @Override
    public Worker move(long workerId, long organizationIdFrom, long organizationIdTo) {
        log.debug("HR move workerId={}, from={}, to={}", workerId, organizationIdFrom, organizationIdTo);
        if (organizationIdFrom == organizationIdTo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id-from и id-to не должны совпадать");
        }
        Worker worker = workerApiClient.getById(workerId);
        if (!Objects.equals(worker.getOrganizationId(), organizationIdFrom)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Работник не состоит в организации id-from=" + organizationIdFrom
                            + " (текущая organizationId=" + worker.getOrganizationId() + ")"
            );
        }
        worker.setOrganizationId(organizationIdTo);
        return workerApiClient.update(workerId, worker);
    }
}
