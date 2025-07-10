package idc.inbound.entity.vision;

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
@Table(name = "role", schema = "vision_idc", uniqueConstraints = {
        @UniqueConstraint(name = "role_name_key", columnNames = {"name"})
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "weight", nullable = false)
    private Integer weight;

    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();

}