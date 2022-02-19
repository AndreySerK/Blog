package main.service;

import com.sun.istack.NotNull;
import main.DTO.*;
import main.model.Post;
import main.model.enums.ModerationStatus;
import main.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPostsWithLimitAndOffset (int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent();
    }

    public List<Post> getPostsWithLimitAndOffsetByDate (int offset, int limit, String date) {

        return getPostsWithLimitAndOffset(offset, limit)
                .stream()
                .filter(post -> post.getTime().toString().contains(date))
                .collect(Collectors.toList());
    }

    public List<Post> getPostsWithLimitAndOffsetByTag (int offset, int limit, String tag) {
        List<Post> posts = new ArrayList<>();
        getPostsWithLimitAndOffset(offset, limit)
                .forEach(post -> {
                    post.getTags()
                            .forEach(tag1 -> {
                                if (tag1.getName().contains(tag)) {
                                    posts.add(post);
                                }
                            });
                });
        return posts;
    }

    public List<Post> getActiveAcceptedPostsBeforeCurrentTime () {
        return postRepository
                .findAll()
                .stream()
                .filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                        && post.getIsActive() == 1
                        && post.getTime().before(new Date()))
                .collect(Collectors.toList());
    }

    public Post getActiveAcceptedPostByIdBeforeCurrentTime (Integer id) {
        if (postRepository.existsById(id)) {
            Post postById = postRepository.getOne(id);
            if (postById.getTime().before(new Date())
                    && postById.getIsActive() == 1
                    && postById.getModerationStatus().equals(ModerationStatus.ACCEPTED)) {
                return postById;
            }
            return null;
        }
        return null;
    }

    public PostByIdDto getPostByIdDto (Integer id) {
        Post post = getActiveAcceptedPostByIdBeforeCurrentTime(id);
        if (post == null) {return null;}
        PostByIdDto postByIdDto= new PostByIdDto();
        UserForPostDto userForPostDto = new UserForPostDto();
        postByIdDto.setId(post.getId());
        postByIdDto.setTimestamp(post.getTime().getTime()/1000);
        postByIdDto.setActive(true);
        userForPostDto.setId(post.getUserId());
        userForPostDto.setName(post.getUser().getName());
        postByIdDto.setUser(userForPostDto);
        postByIdDto.setTitle(post.getTitle());
        postByIdDto.setText(post.getText());
        postByIdDto.setDislikeCount
                ((int) post.getVoteList()
                        .stream()
                        .filter(vote -> vote.getValue() < 0).count()
                );
        postByIdDto.setLikeCount
                ((int) post.getVoteList()
                        .stream()
                        .filter(vote -> vote.getValue() > 0)
                        .count()
                );
        postByIdDto.setViewCount(post.getViewCount());
        List<CommentForPostByIdDto> commentsForPostByIdDtoList = new ArrayList<>();
        post.getPostCommentList()
                .forEach(postComment -> {
                     CommentForPostByIdDto comment = new CommentForPostByIdDto();
                     UserForCommentDto user = new UserForCommentDto();
                     comment.setId(postComment.getId());
                     comment.setTimestamp(postComment.getTime().getTime()/1000);
                     comment.setText(postComment.getText());
                     user.setId(postComment.getUserId());
                     user.setName(postComment.getUser().getName());
                     user.setPhoto(postComment.getUser().getPhoto());
                     comment.setUser(user);
                     commentsForPostByIdDtoList.add(comment);
                });
        postByIdDto.setComments(commentsForPostByIdDtoList);
        List<TagForPostByIdDto> tagForPostByIdDtoList = new ArrayList<>();
        post.getTags().forEach(tag -> {
            TagForPostByIdDto tagForPostByIdDto = new TagForPostByIdDto();
            tagForPostByIdDto.setName(tag.getName());
            tagForPostByIdDtoList.add(tagForPostByIdDto);
        });
        postByIdDto.setTags(tagForPostByIdDtoList);
        return postByIdDto;
    }

    public List<Integer> getYearsOfPosts() {
        Set<Integer> yearsSet = new HashSet<>();
        getActiveAcceptedPostsBeforeCurrentTime()
                .forEach(post -> {
                    yearsSet.add(post.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
                });
        List<Integer> years = new ArrayList<>(yearsSet);
        Collections.sort(years);
        return years;
    }

    public Map<String, Integer> getPostsCountOnDate(Integer year)  {
        Map<String, Integer> postsMap = new HashMap<>();
        List<Post> postsOfYear = new ArrayList<>();
        Set<String> days = new HashSet<>();
        getActiveAcceptedPostsBeforeCurrentTime()
                .stream()
                .filter(post -> post.getTime().toString().contains(year.toString()))
                .forEach(post -> {
                    days.add(post.getTime().toString().substring(0,10));
                    postsOfYear.add(post);
                });
        days.forEach(day -> {
            postsMap.put(day,
                    (int) postsOfYear.stream()
                                    .filter(post -> post.getTime().toString().contains(day))
                                    .count());
                });
        return postsMap;
    }

    public Integer getCountOfAllPosts () {return postRepository.findAll().size();}

    public Integer getCountOfPostsWithQuery (String query) {
        List <Post> postsWithQuery = new ArrayList<>();
        List <Post> posts =  postRepository.findAll();
        posts.forEach(post -> {
            if (post.getText().contains(query)) {
                postsWithQuery.add(post);
            }
        });
        return postsWithQuery.size();
    }

    public List<PostDto> getListOfPostDto(@NotNull List <Post> posts ) {
        List<PostDto> postDtoList = new ArrayList<>();
        posts.stream()
                .filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                        && post.getIsActive() == 1
                        && post.getTime().before(new Date()))
                .forEach(post -> {
            PostDto postDto = new PostDto();
            UserForPostDto user = new UserForPostDto();
            user.setId(post.getUserId());
            user.setName(post.getUser().getName());
            postDto.setUser(user);
            postDto.setId(post.getId());
            postDto.setTimestamp(post.getTime().getTime()/1000);
            postDto.setTitle(post.getTitle());
            postDto.setCommentCount(post.getPostCommentList().size());
            postDto.setDislikeCount
                    ((int) post.getVoteList()
                            .stream()
                            .filter(vote -> vote.getValue() < 0).count()
                    );
            postDto.setLikeCount
                    ((int) post.getVoteList()
                            .stream()
                            .filter(vote -> vote.getValue() > 0)
                            .count()
                    );
            postDto.setViewCount(post.getViewCount());
            String announce = post.getText();
            if (announce.length() < 150) {
                postDto.setAnnounce(announce);
            } else {
                postDto.setAnnounce(announce.substring(0,149) + "...");
            }
            postDtoList.add(postDto);
        });
        return postDtoList;
    }

    public List<PostDto> getListOfPostDtoWithQuery (int offset, int limit, String query) {
        List <Post> postsWithQuery = getPostsWithLimitAndOffset(offset, limit)
                .stream()
                .filter(post -> post.getText().contains(query))
                .collect(Collectors.toList());
        return getListOfPostDto(postsWithQuery);
    }

    public List<PostDto> getSortedListOfPostDtoByMode (int offset, int limit, String mode) {
        List <Post> postList = getPostsWithLimitAndOffset(offset, limit);
        List<PostDto> postDtoList = getListOfPostDto(postList);
        if (mode.equals("early")) {
            postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp));
            return postDtoList;
        }
        if (mode.equals("popular")) {
            postDtoList.sort(Comparator.comparingInt(PostDto::getCommentCount).reversed());
            return postDtoList;
        }
        if (mode.equals("best")) {
            postDtoList.sort(Comparator.comparingInt(PostDto::getLikeCount).reversed());
            return postDtoList;
        }
        postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp).reversed());
        return postDtoList;
    }

    public List<PostDto> getListOfPostDtoByDate (int offset, int limit, String date) {
        return getListOfPostDto(getPostsWithLimitAndOffsetByDate(offset, limit, date));
    }

    public List<PostDto> getListOfPostDtoByTag (int offset, int limit, String tag) {
        return getListOfPostDto(getPostsWithLimitAndOffsetByTag(offset, limit, tag));
    }

}


