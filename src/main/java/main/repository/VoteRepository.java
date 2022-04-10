package main.repository;

import main.model.Post;
import main.model.User;
import main.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    List<Vote> getVotesByUser(User user);

    Vote getVoteByUserAndPost(User user, Post post);
}
