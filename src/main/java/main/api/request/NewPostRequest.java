package main.api.request;

import lombok.Data;
import main.model.Tag;

import java.sql.Timestamp;
import java.util.List;

@Data
public class NewPostRequest {

    private long timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
