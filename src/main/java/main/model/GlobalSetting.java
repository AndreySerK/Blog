package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import main.model.enums.Code;
import main.model.enums.Value;

import javax.persistence.*;

@Entity
@Data
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @Column(columnDefinition = "VARCHAR(255)")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Code code;

    @Column(columnDefinition = "VARCHAR(255)")
    @NotNull
    private String name;

    @Column(columnDefinition = "VARCHAR(255)")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Value value;
}
