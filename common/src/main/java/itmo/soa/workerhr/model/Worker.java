package itmo.soa.workerhr.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
@JacksonXmlRootElement(localName = "Worker")
public class Worker {

    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Valid
    private Coordinates coordinates;

    private LocalDate creationDate;

    @Positive
    private float salary;

    private Position position;

    @NotNull
    private Status status;

    @Valid
    private Person person;
}
