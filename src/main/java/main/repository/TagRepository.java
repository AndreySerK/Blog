package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository <Tag, Integer> {

    @Query (value = "SELECT COUNT(t.tag_id) " +
                    "FROM tag2post t " +
                    "GROUP BY tag_id " +
                    "ORDER BY tag_id " +
                    "DESC LIMIT 1"
                    ,nativeQuery = true)
    Double getCountOfMostPopularTag ();
    Tag getTagByName(String name);
}
