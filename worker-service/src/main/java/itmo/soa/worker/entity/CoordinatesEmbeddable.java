package itmo.soa.worker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesEmbeddable {

    @NotNull
    @DecimalMin(value = "-250", inclusive = false)
    @Column(name = "coordinates_x", nullable = false)
    private Double x;

    @Column(name = "coordinates_y", nullable = false)
    private float y;
}
