package itmo.soa.worker.entity;

import itmo.soa.workerhr.model.Country;
import itmo.soa.workerhr.model.EyeColor;
import itmo.soa.workerhr.model.HairColor;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persons")
@Getter @Setter
@NoArgsConstructor
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer height;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", length = 32)
    private EyeColor eyeColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", length = 32)
    private HairColor hairColor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Country nationality;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y")),
            @AttributeOverride(name = "z", column = @Column(name = "location_z")),
            @AttributeOverride(name = "name", column = @Column(name = "location_name"))
    })
    private LocationEmbeddable location;
}
