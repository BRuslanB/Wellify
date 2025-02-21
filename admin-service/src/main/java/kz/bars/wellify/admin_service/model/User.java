package kz.bars.wellify.admin_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    // Используем Keycloak user id как идентификатор
    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    // Дополнительные поля
    private String phone;
    private String address;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE; // По умолчанию активен

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Перечисление, содержащее возможные стутасы пользователя.
     */
    public enum UserStatus {
        ACTIVE,   // Пользователь активен
        BLOCKED,  // Заблокирован
        DELETED   // Мягко удален
    }
}
