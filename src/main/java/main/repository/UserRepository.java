package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT COUNT('*') " +
            "FROM posts " +
            "WHERE moderation_status = 'NEW'", nativeQuery = true)
    Integer getPostsForModerationCount();

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);
}
