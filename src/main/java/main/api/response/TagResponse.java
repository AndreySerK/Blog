package main.api.response;

import lombok.Getter;
import lombok.Setter;
import main.DTO.TagDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
public class TagResponse {

    private List<TagDto> tags;
}
