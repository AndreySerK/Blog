package main.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @Column(name = "parent_id")
    private int parentId;


}
