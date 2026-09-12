package itmo.soa.workerhr.model.error;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "MethodNotAllowedError")
public class MethodNotAllowedError {

    private int status;
    private String errorCode;
    private String message;
    private String method;
    private String supportedMethods;
}
