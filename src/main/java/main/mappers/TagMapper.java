package main.mappers;

import main.DTO.TagDto;
import main.DTO.TagForPostByIdDto;
import main.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper( TagMapper.class );
    TagForPostByIdDto tagToTagForPostByIdDto (Tag tag);
    TagDto tagToTagDto (Tag tag);
}
