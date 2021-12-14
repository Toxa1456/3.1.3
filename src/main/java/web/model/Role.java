package web.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

// Этот класс реализует интерфейс GrantedAuthority, в котором необходимо переопределить только один метод getAuthority() (возвращает имя роли).
// Имя роли должно соответствовать шаблону: «ROLE_ИМЯ», например, ROLE_USER.
@Entity
@Table (name = "Role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role", unique = true)
    private String role;

    @ManyToMany (mappedBy = "roles")
    private Set<User> user;

    public Role(){
    }

    public Role(String role) {
        this.role = "ROLE_" + role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role.replace("ROLE_", "");
    }

    public void setRole(String role) {
        this.role = "ROLE_" + role;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
