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
@Table(name = "bram", schema = "unloading", uniqueConstraints = {
        @UniqueConstraint(name = "bram_name_key", columnNames = {"name"})
})
public class Bram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 128)
    private StatusBramAndRamp status;

    @ColumnDefault("1")
    @Column(name = "max_buffer")
    private Integer maxBuffer;

    @ColumnDefault("0")
    @Column(name = "actual_buffer")
    private Integer actualBuffer;

    @OneToMany(mappedBy = "bram")
    private Set<Booking> bookings = new LinkedHashSet<>();

}