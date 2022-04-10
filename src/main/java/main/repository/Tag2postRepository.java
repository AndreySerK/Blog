package main.repository;

import main.model.Tag2post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface Tag2postRepository extends JpaRepository<Tag2post, Integer> {

}
