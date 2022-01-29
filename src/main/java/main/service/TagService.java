package main.service;
import main.DTO.TagDto;
import main.model.Tag;
import main.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final PostService postService;

    public TagService(TagRepository tagRepository, PostService postService) {
        this.tagRepository = tagRepository;
        this.postService = postService;
    }

    public List <Tag> getAllTags () {
        return (List<Tag>) tagRepository.findAll();
    }

    public Double getCountOfMostPopularTag () {
        List <Integer> tag2postCounts = new ArrayList<>();
        getAllTags().forEach(tag -> {
            tag2postCounts.add(tag.getPosts().size());
        });
        tag2postCounts.sort(Collections.reverseOrder());
        return Double.valueOf(tag2postCounts.get(0));
    }
    public Double getWeightOfTag (Tag tag) {
        double tag2postCount  = tag.getPosts().size();
        double allPostCount = postService.getCountOfPosts();
        double dWeightTag = tag2postCount/allPostCount;
        double countOfMostPopularTag = getCountOfMostPopularTag();
        double dWeightMax = countOfMostPopularTag/allPostCount;
        double k = 1/dWeightMax;
        return dWeightTag*k;
    }

    public List<TagDto> getAllTagDtoByQuery (String query) {
        List <TagDto> tagDtoList = new ArrayList<>();
        List<Tag> tagList = getAllTags();
        tagList.forEach
                (tag -> {
                    int queryLength = query.length();
                    TagDto tagDto = new TagDto();
                    if (tag.getName().substring(0, queryLength - 1).equals(query)) {
                        tagDto.setName(tag.getName());
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
                    TagDto tagDto = new TagDto();
                    tagDto.setName(tag.getName());
                    tagDto.setWeight(getWeightOfTag(tag));
                    tagDtoList.add(tagDto);

                });
        return tagDtoList;
    }

}
