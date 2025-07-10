package idc.inbound.entity.unloading;

import idc.inbound.entity.vision.User;
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
@Table(name = "product_type", schema = "unloading", uniqueConstraints = {
        @UniqueConstraint(name = "product_type_name_key", columnNames = {"name"})
})
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 128)
    @Column(name = "name", length = 128)
    private String name;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @OneToMany(mappedBy = "productType")
    private Set<Booking> bookings = new LinkedHashSet<>();

}