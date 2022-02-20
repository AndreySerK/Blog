package main.service;
import lombok.RequiredArgsConstructor;
import main.DTO.TagDto;
import main.mappers.TagMapper;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    private List <Tag> getAllTags () {
        return (List<Tag>) tagRepository.findAll();
    }

    private Double getWeightOfTag (Tag tag) {
        double tag2postCount  = tag.getPosts().size();
        long allPostCount = postRepository.count();
        double dWeightTag = tag2postCount/allPostCount;
        double countOfMostPopularTag = tagRepository.getCountOfMostPopularTag();
        double dWeightMax = countOfMostPopularTag/allPostCount;
        double k = 1/dWeightMax;
        return dWeightTag*k;
    }

    public List<TagDto> getAllTagDtoByQuery (String query) {
        List <TagDto> tagDtoList = new ArrayList<>();
        List<Tag> tagList = getAllTags();
        tagList.forEach
                (tag -> {
                    TagDto tagDto = TagMapper.INSTANCE.tagToTagDto(tag);
                    if (query.length() <= tag.getName().length() && tag.getName().contains(query)) {
                        tagDto.setWeight(getWeightOfTag(tag));
                        tagDtoList.add(tagDto);
                    }
                });
        return tagDtoList;
    }

    public List<TagDto> getAllTagDto () {
        List <TagDto> tagDtoList = new ArrayList<>();
        List<Tag> tagList = getAllTags();
        tagList.forEach
                (tag -> {
                    TagDto tagDto = TagMapper.INSTANCE.tagToTagDto(tag);
                    tagDto.setWeight(getWeightOfTag(tag));
                    tagDtoList.add(tagDto);
                });
        return tagDtoList;
    }

}
