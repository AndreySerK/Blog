package main.mappers;

import main.DTO.TagDto;
import main.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagDto tagToTagDto(Tag tag);
}
