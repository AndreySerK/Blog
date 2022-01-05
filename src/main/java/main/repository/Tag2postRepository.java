package main.repository;

import main.model.Tag2post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2postRepository extends CrudRepository <Tag2post,Integer> {
}
