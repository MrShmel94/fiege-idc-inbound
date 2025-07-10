package idc.inbound.entity.unloading;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ramp", schema = "unloading", uniqueConstraints = {
        @UniqueConstraint(name = "ramp_name_key", columnNames = {"name"})
})
public class Ramp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status", nullable = false, length = 128)
    private StatusBramAndRamp status;

    @ColumnDefault("1")
    @Column(name = "max_buffer")
    private Integer maxBuffer;

    @ColumnDefault("0")
    @Column(name = "actual_buffer")
    private Integer actualBuffer;

    @OneToMany(mappedBy = "ramp")
    private Set<Booking> bookings = new LinkedHashSet<>();
}