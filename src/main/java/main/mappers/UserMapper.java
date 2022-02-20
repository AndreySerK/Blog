package main.mappers;

import main.DTO.AuthUserDto;
import main.DTO.UserForCommentDto;
import main.DTO.UserForPostDto;
import main.model.User;
import main.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @Mapping(target = "moderation", source = "isModerator")
    AuthUserDto userToAuthUserDto (User user);

    UserForCommentDto userToUserForCommentsDto (User user);

    UserForPostDto userToUserForPostDto (User user);
}
