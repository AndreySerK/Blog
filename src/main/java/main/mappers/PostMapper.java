package main.mappers;

import main.DTO.PostByIdDto;
import main.DTO.PostDto;
import main.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostDto postToPostDto(Post post);

    @Mapping(target = "tags", ignore = true)
    PostByIdDto postToPostByIdDto(Post post);
}
