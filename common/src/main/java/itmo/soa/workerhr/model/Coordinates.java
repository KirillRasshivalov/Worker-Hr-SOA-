package itmo.soa.workerhr.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "coordinates")
public class Coordinates {

    @NotNull
    @DecimalMin(value = "-250", inclusive = false)
    private Double x;

    private float y;
}
