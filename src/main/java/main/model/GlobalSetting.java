package main.model;

import com.sun.istack.NotNull;
import lombok.*;
import main.model.enums.Value;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id;

    @Column (columnDefinition = "VARCHAR(255)")
    @NotNull
    private String code;

    @Column (columnDefinition = "VARCHAR(255)")
    @NotNull
    private String name;

    @Column (columnDefinition = "VARCHAR(255)")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Value value;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
