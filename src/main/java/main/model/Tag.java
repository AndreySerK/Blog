package main.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Post> posts;
}
