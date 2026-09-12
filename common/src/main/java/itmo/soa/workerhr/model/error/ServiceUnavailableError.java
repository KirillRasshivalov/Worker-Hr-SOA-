package itmo.soa.workerhr.model.error;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "ServiceUnavailableError")
public class ServiceUnavailableError {

    private int status;
    private String errorCode;
    private String message;
    private String dependency;
}
