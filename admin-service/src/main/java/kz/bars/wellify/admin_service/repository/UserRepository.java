package kz.bars.wellify.admin_service.repository;

import kz.bars.wellify.admin_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByIdAndStatus(String id, User.UserStatus status);
}
