package main.mappers;

import main.DTO.CommentForPostByIdDto;
import main.model.PostComment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
    CommentForPostByIdDto commentForPostByIdDto (PostComment comment);
}
