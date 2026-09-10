package itmo.soa.worker.entity;

import itmo.soa.workerhr.model.Position;
import itmo.soa.workerhr.model.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "workers")
@Getter @Setter
@NoArgsConstructor
public class WorkerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Valid
    @Embedded
    private CoordinatesEmbeddable coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate;

    @Positive
    @Column(nullable = false)
    private float salary;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    private Position position;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private Status status;

    /**
     * Организация сотрудника (для /hr/move). Может быть null до назначения.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    /**
     * Person опционален. Cascade ALL + orphanRemoval — при удалении Worker удаляется и Person.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", unique = true)
    private PersonEntity person;

    @PrePersist
    void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDate.now();
        }
    }
}
