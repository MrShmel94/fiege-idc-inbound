package idc.inbound.entity.vision;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "department", schema = "vision_idc", uniqueConstraints = {
        @UniqueConstraint(name = "department_name_site_id_key", columnNames = {"name", "site_id"})
})

public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 128)
    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @OneToMany(mappedBy = "department")
    private Set<User> users = new LinkedHashSet<>();

}