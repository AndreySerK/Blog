package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.*;
import main.mappers.CommentMapper;
import main.mappers.PostMapper;
import main.mappers.TagMapper;
import main.mappers.UserMapper;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private List<Post> getPostsWithLimitAndOffset (int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent();
    }

    private List<Post> getPostsWithLimitAndOffsetByModeratorId (int offset, int limit, String email) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent()
                .stream()
                .filter(post -> post.getModeratorId() == getCurrentUserIdByEmail(email))
                .collect(Collectors.toList());
    }

    private Integer getCurrentUserIdByEmail (String email) {
       return   userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("not found")).getId();
    }

    private List<Post> getPostsWithLimitAndOffsetByDate (int offset, int limit, String date) {

        return getPostsWithLimitAndOffset(offset, limit)
                .stream()
                .filter(post -> post.getTime().toString().contains(date))
                .collect(Collectors.toList());
    }

    private List<Post> getPostsWithLimitAndOffsetByTag (int offset, int limit, String tag) {
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

    private List<Post> getActiveAcceptedPostsBeforeCurrentTime () {
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
        int mSecCountInSec = 1000;
        if (post == null) {
            return null;
        }
        PostByIdDto postByIdDto= PostMapper.INSTANCE.postToPostByIdDto(post);
        UserForPostDto userForPostDto = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
        long timestampInSeconds = post.getTime().getTime()/mSecCountInSec;
        postByIdDto.setTimestamp(timestampInSeconds);
        postByIdDto.setActive(true);
        postByIdDto.setUser(userForPostDto);
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
        List<CommentForPostByIdDto> commentsForPostByIdDtoList = new ArrayList<>();
        post.getPostCommentList()
                .forEach(postComment -> {
                     CommentForPostByIdDto comment = CommentMapper.INSTANCE.commentForPostByIdDto(postComment);
                     UserForCommentDto user = UserMapper.INSTANCE.userToUserForCommentsDto(post.getUser());
                     comment.setTimestamp(postComment.getTime().getTime()/mSecCountInSec);
                     comment.setUser(user);
                     commentsForPostByIdDtoList.add(comment);
                });
        postByIdDto.setComments(commentsForPostByIdDtoList);
        List<TagForPostByIdDto> tagForPostByIdDtoList = new ArrayList<>();
        post.getTags().forEach(tag -> {
            TagForPostByIdDto tagForPostByIdDto = TagMapper.INSTANCE.tagToTagForPostByIdDto(tag);
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

    public Integer getCountOfAllPosts () {
        return postRepository.getCountOfAllPosts();
    }

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

    private List<PostDto> getListOfPostDto(List <Post> posts, String moderationStatus, int isActive, Date date) {
        List<PostDto> postDtoList = new ArrayList<>();
        int mSecCountInSec = 1000;
        posts.stream()
                .filter(post -> post.getModerationStatus().toString().toLowerCase().equals(moderationStatus)
                        && post.getIsActive() == isActive
                        && post.getTime().before(date))
                .forEach(post -> {
            PostDto postDto = PostMapper.INSTANCE.postToPostDto(post);
            UserForPostDto user = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
            postDto.setUser(user);
            postDto.setTimestamp(post.getTime().getTime()/mSecCountInSec);
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
            String announce = post.getText();
            int announceMaxLength = 150;
            if (announce.length() < announceMaxLength) {
                postDto.setAnnounce(announce);
            } else {
                postDto.setAnnounce(announce.substring(0,149) + "...");
            }
            postDtoList.add(postDto);
        });
        return postDtoList;
    }

    public List<Post> getListOfPostByUserId (int offset, int limit) {
        int currentUserId = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("user not found")).getId();
        return getPostsWithLimitAndOffset(offset, limit)
                .stream()
                .filter(post -> post.getUserId() == currentUserId)
                .collect(Collectors.toList());
    }

    public List<PostDto> getListOfPostDtoByUserId (List <Post> posts, int isActive) {
        List<PostDto> postDtoList = new ArrayList<>();
        int mSecCountInSec = 1000;
        posts.stream()
                .filter(post -> post.getIsActive() == isActive)
                .forEach(post -> {
                    PostDto postDto = PostMapper.INSTANCE.postToPostDto(post);
                    UserForPostDto user = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
                    postDto.setUser(user);
                    postDto.setTimestamp(post.getTime().getTime()/mSecCountInSec);
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
                    String announce = post.getText();
                    int announceMaxLength = 150;
                    if (announce.length() < announceMaxLength) {
                        postDto.setAnnounce(announce);
                    } else {
                        postDto.setAnnounce(announce.substring(0,149) + "...");
                    }
                    postDtoList.add(postDto);
                });
        return postDtoList;
    }

    public List<PostDto> getListOfPostDtoByUserId (List <Post> posts, int isActive, String moderationStatus) {
        List<PostDto> postDtoList = new ArrayList<>();
        int mSecCountInSec = 1000;
        posts.stream()
                .filter(post -> post.getIsActive() == isActive
                        && post.getModerationStatus().toString().toLowerCase().equals(moderationStatus))
                .forEach(post -> {
                    PostDto postDto = PostMapper.INSTANCE.postToPostDto(post);
                    UserForPostDto user = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
                    postDto.setUser(user);
                    postDto.setTimestamp(post.getTime().getTime()/mSecCountInSec);
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
                    String announce = post.getText();
                    int announceMaxLength = 150;
                    if (announce.length() < announceMaxLength) {
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
        return getListOfPostDto(postsWithQuery,"accepted",1,new Date());
    }

    public List<PostDto> getSortedListOfPostDtoByMode (int offset, int limit, String mode) {
        List <Post> postList = getPostsWithLimitAndOffset(offset, limit);
        List<PostDto> postDtoList = getListOfPostDto(postList, "accepted", 1, new Date());
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
        return getListOfPostDto(getPostsWithLimitAndOffsetByDate(offset, limit, date), "accepted",1,new Date());
    }

    public List<PostDto> getListOfPostDtoByModerationStatus(int offset, int limit, String status) {
        return getListOfPostDto(getPostsWithLimitAndOffset(offset, limit), status,1, new Date());
    }

    public List<PostDto> getListOfPostDtoByModerationStatusAndId (int offset, int limit, String status) {

        return getListOfPostDto(getPostsWithLimitAndOffsetByModeratorId(offset, limit, getCurrentUserEmail()), status,1,new Date());
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public List<PostDto> getListOfPostDtoByTag (int offset, int limit, String tag) {
        return getListOfPostDto(getPostsWithLimitAndOffsetByTag(offset, limit, tag), "accepted",1,new Date());
    }

}


