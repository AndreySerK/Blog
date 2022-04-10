package main.api.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
public class CalendarOfPostsResponse {

    private List<Integer> years;
    private Map<String, Integer> posts;
}
