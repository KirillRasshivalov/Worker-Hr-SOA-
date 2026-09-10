package itmo.soa.worker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEmbeddable {

    @Column(name = "location_x")
    private Long x;

    @Column(name = "location_y")
    private Float y;

    @Column(name = "location_z")
    private Integer z;

    @Column(name = "location_name")
    private String name;
}
