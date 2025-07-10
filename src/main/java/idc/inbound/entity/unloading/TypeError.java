package idc.inbound.entity.unloading;

import idc.inbound.entity.vision.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "type_error", schema = "unloading", uniqueConstraints = {
        @UniqueConstraint(name = "type_error_name_key", columnNames = {"name"})
})
public class TypeError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;
}