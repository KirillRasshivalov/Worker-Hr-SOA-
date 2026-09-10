package itmo.soa.workerhr.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "location")
public class Location {

    private long x;
    private float y;
    private int z;
    private String name;
}
