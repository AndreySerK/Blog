package main.mappers;

import main.DTO.AuthUserDto;
import main.DTO.UserForCommentDto;
import main.DTO.UserForPostDto;
import main.api.response.UserLoginResponse;
import main.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    AuthUserDto userToAuthUserDto (User user);

    UserLoginResponse userToUserResponse (User user);

    UserForCommentDto userToUserForCommentsDto (User user);

    UserForPostDto userToUserForPostDto (User user);
}
