package itmo.soa.hr.service;

import itmo.soa.workerhr.model.Worker;

public interface HrService {

    Worker fire(long workerId);

    Worker move(long workerId, long organizationIdFrom, long organizationIdTo);
}
