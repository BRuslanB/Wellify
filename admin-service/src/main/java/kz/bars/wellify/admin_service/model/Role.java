package kz.bars.wellify.admin_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description; // Дополнительное поле, описание роли

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * Перечисление, содержащее возможные роли.
     * Эти роли должны быть созданы в Keycloak.
     */
    public enum RoleName {
        USER, // используется по умолчанию при создание пользователя
        ADMIN
    }
}
