package itmo.soa.workerhr.model.error;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldViolation {

    @JacksonXmlProperty(localName = "field")
    private String field;

    @JacksonXmlProperty(localName = "message")
    private String message;
}
