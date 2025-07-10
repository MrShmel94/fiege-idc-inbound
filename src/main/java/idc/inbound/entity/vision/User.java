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
@Table(name = "users", schema = "vision_idc", uniqueConstraints = {
        @UniqueConstraint(name = "users_expertis_key", columnNames = {"expertis"}),
        @UniqueConstraint(name = "users_login_key", columnNames = {"login"}),
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 256)
    @NotNull
    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Size(max = 256)
    @NotNull
    @Column(name = "second_name", nullable = false, length = 256)
    private String secondName;

    @Size(max = 128)
    @NotNull
    @Column(name = "expertis", nullable = false, length = 128)
    private String expertis;

    @Size(max = 128)
    @NotNull
    @Column(name = "login", nullable = false, length = 128)
    private String login;

    @ColumnDefault("true")
    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;

    @Size(max = 512)
    @NotNull
    @Column(name = "encrypted_password", nullable = false, length = 512)
    private String encryptedPassword;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "user")
    private Set<LoginHistory> loginHistories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdByUser")
    private Set<User> users = new LinkedHashSet<>();

}