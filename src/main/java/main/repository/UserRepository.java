package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query (value = "SELECT COUNT('*') " +
            "FROM posts " +
            "WHERE moderation_status = 'NEW'", nativeQuery = true)
    Integer getPostsForModerationCount ();
}
