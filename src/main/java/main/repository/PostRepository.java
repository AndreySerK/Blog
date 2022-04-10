package main.repository;

import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
    Integer getCountOfAllPosts();

    List<Post> getPostsByUserId(int userId);

    Post getPostById(int id);

    List<Post> getPostsByModerationStatus(ModerationStatus moderationStatus);
}
