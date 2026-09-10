package itmo.soa.workerhr.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "person")
public class Person {

    @NotNull
    @Positive
    private Integer height;

    private EyeColor eyeColor;
    private HairColor hairColor;

    @NotNull
    private Country nationality;

    @Valid
    private Location location;
}
