package itmo.soa.worker.services;

import itmo.soa.workerhr.model.Worker;
import itmo.soa.workerhr.model.WorkerPage;

import java.util.Map;

public interface WorkerService {

    void addNewWorker(Worker worker);

    Worker getWorkerById(long id);

    Worker updateWorker(long id, Worker worker);

    void deleteWorker(long id);

    WorkerPage getAllWorkers(Map<String, String> filters, int page, int size, String sort, String order);
}
