package itmo.soa.worker.services;

import itmo.soa.workerhr.model.Person;

public interface ExtraCommandService {

    void deleteAllWorkersWithPerson(Person person);

    void deleteWorkerWithSalary(float salary);

    double averageSalaryByOrganization();
}
