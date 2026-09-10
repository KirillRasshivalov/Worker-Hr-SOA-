package itmo.soa.hr.client;

import itmo.soa.workerhr.model.Worker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WorkerApiClient {

    private final RestClient restClient;

    public WorkerApiClient(
            RestClient.Builder builder,
            @Value("${worker-service.base-url}") String baseUrl
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_XML_VALUE)
                .build();
    }

    public Worker getById(long id) {
        return restClient.get()
                .uri("/workers/{id}", id)
                .retrieve()
                .body(Worker.class);
    }

    public Worker update(long id, Worker worker) {
        return restClient.put()
                .uri("/workers/{id}", id)
                .contentType(MediaType.APPLICATION_XML)
                .body(worker)
                .retrieve()
                .body(Worker.class);
    }
}
