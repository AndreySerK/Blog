package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.*;
import main.api.request.AddCommentRequest;
import main.api.request.NewPostRequest;
import main.api.request.PostModerateRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.mappers.CommentMapper;
import main.mappers.PostMapper;
import main.mappers.TagMapper;
import main.mappers.UserMapper;
import main.model.*;
import main.model.enums.Code;
import main.model.enums.ModerationStatus;
import main.model.enums.Value;
import main.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final Tag2postRepository tag2postRepository;
    private final GlobalSettingRepository globalSettingRepository;
    private final PostCommentRepository postCommentRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private Date timestampToDate (Timestamp timestamp) {
        return  new Date(timestamp.getTime());
    }

    private List<Post> getPostsWithLimitAndOffset (int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent();
    }

    private List<Post> getPostsWithLimitAndOffsetByModeratorId (int offset, int limit, String email) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent()
                .stream()
                .filter(post -> post.getModeratorId() == getCurrentUserIdByEmail(email)
                || post.getModerationStatus().equals(ModerationStatus.NEW))
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

    public void addViewToPost (Integer id) {
        Post post = getActiveAcceptedPostByIdBeforeCurrentTime(id);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
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

    public NewPostResponse addNewPost (NewPostRequest newPostRequest, Principal principal) {
        NewPostResponse postsResponse = new NewPostResponse();
        NewPostErrors newPostErrors = new NewPostErrors();
        if (newPostRequest.getTitle().length() < 3 || newPostRequest.getTitle() == null) {
            newPostErrors.setTitle("Заголовок не установлен");
        }
        if (newPostRequest.getText().length() < 50 || newPostRequest.getText() == null) {
            newPostErrors.setText("Текст публикации слишком короткий");
        }
        if (newPostRequest.getTitle().length() > 2 && newPostRequest.getText().length() > 49) {
            Post newPost = new Post();
            if (newPostRequest.getTimestamp().before(new Date())) {
                newPost.setTime(new Date());
            } else {
                newPost.setTime(timestampToDate(newPostRequest.getTimestamp()));
            }
            newPost.setIsActive(newPostRequest.getActive());
            if (globalSettingRepository.findByCode(Code.POST_PREMODERATION).getValue().equals(Value.YES)) {
                newPost.setModerationStatus(ModerationStatus.NEW);
            } else if (newPost.getIsActive() == 1) {
                newPost.setModerationStatus(ModerationStatus.ACCEPTED);
            }
            newPost.setTitle(newPostRequest.getTitle());
            newPost.setText(newPostRequest.getText());
            newPost.setUser(userRepository.findByEmail(principal.getName()).orElseThrow());
            newPost.setViewCount(0);
            postRepository.save(newPost);
            Tag2post tag2post = new Tag2post();
            if (newPostRequest.getTags().size() > 0) {
                newPostRequest.getTags().forEach(t -> {
                    Tag tag = tagRepository.getTagByName(t);
                    if (tag == null) {
                        Tag newTag = new Tag();
                        newTag.setName(t);
                        tagRepository.save(newTag);
                        tag2post.setTagId(tagRepository.getTagByName(t).getId());
                    } else {
                        tag2post.setTagId(tag.getId());
                    }
                    tag2post.setPostId(newPost.getId());
                    tag2postRepository.save(tag2post);
                });
            }
            postsResponse.setResult(true);
            return postsResponse;
        }
        postsResponse.setResult(false);
        postsResponse.setErrors(newPostErrors);
        return postsResponse;
    }

    public NewPostResponse changePost (NewPostRequest newPostRequest, Principal principal, int id) {
        NewPostResponse postResponse = new NewPostResponse();
        NewPostErrors newPostErrors = new NewPostErrors();
        if (newPostRequest.getTitle().length() < 3 || newPostRequest.getTitle() == null) {
            newPostErrors.setTitle("Заголовок не установлен");
        }
        if (newPostRequest.getText().length() < 50 || newPostRequest.getText() == null) {
            newPostErrors.setText("Текст публикации слишком короткий");
        }
        if (newPostRequest.getTitle().length() > 2 && newPostRequest.getText().length() > 49) {
            Post post = postRepository.findById(id).orElseThrow(() -> new FindException("Пост с таки id не найден"));
            if (newPostRequest.getTimestamp().before(new Date())) {
                post.setTime(new Date());
            } else {
                post.setTime(timestampToDate(newPostRequest.getTimestamp()));
            }
            post.setIsActive(newPostRequest.getActive());
            post.setTitle(newPostRequest.getTitle());
            post.setText(newPostRequest.getText());
            if (userRepository.findByEmail(principal.getName()).get().getIsModerator() == 0) {
                post.setModerationStatus(ModerationStatus.NEW);
            }
            postRepository.save(post);
            Tag2post tag2post = new Tag2post();
            if (newPostRequest.getTags().size() > 0) {
            newPostRequest.getTags().forEach(t -> {
                        Tag tag = tagRepository.getTagByName(t);
                        if (tag == null) {
                            Tag newTag = new Tag();
                            newTag.setName(t);
                            tagRepository.save(newTag);
                            tag2post.setTagId(tagRepository.getTagByName(t).getId());
                        } else {
                            tag2post.setTagId(tag.getId());
                        }
                        tag2post.setPostId(post.getId());
                        tag2postRepository.save(tag2post);
                    });
            }
            postResponse.setResult(true);
            return postResponse;
        }
        postResponse.setResult(false);
        postResponse.setErrors(newPostErrors);
        return postResponse;
    }

    public String isCommentAddSuccess (AddCommentRequest request) {

        if (request.getText() == null || request.getText().length() < 5) {
            return "Текст комментария не задан или слишком короткий";
        }
        if (request.getParentId() == null ) {
            if (!postRepository.existsById(request.getPostId())) {
                return "400";
            }
        } else {
            if (!postRepository.existsById(request.getPostId())
                    || !postCommentRepository.existsById(request.getParentId())) {
                return "400";
            }
        }
        return "200";
    }

    public AddCommentResponse getAddCommentResponse (AddCommentRequest request, Principal principal) {
        AddCommentResponse addCommentResponse = new AddCommentResponse();
        PostComment newComment = new PostComment();
        newComment.setText(request.getText());
        newComment.setParentId(request.getParentId());
        newComment.setTime(new Date());
        newComment.setPost(postRepository.findById(request.getPostId()).orElseThrow());
        newComment.setUserId(userRepository.findByEmail(principal.getName()).orElseThrow().getId());
        postCommentRepository.save(newComment);
        addCommentResponse.setId(postCommentRepository.getPostCommentByText(request.getText()).getId());
        return addCommentResponse;
    }

    public AddCommentResponseErr getAddCommentErr (AddCommentRequest request) {
        AddCommentErr addCommentErr = new AddCommentErr();
        AddCommentResponseErr addCommentResponseErr = new AddCommentResponseErr();
        addCommentErr.setText(isCommentAddSuccess(request));
        addCommentResponseErr.setResult(false);
        addCommentResponseErr.setErrors(addCommentErr);
        return addCommentResponseErr;
    }

    public boolean isPostModerateSuccess (PostModerateRequest request, Principal principal) {
        if (userRepository.findByEmail(principal.getName()).orElseThrow().getIsModerator() == 0
        || !postRepository.existsById(request.getPostId())) {
            return false;
        }
        Post postToModerate = postRepository.findById(request.getPostId()).orElseThrow();
        int moderatorId = getCurrentUserIdByEmail(principal.getName());
        if (request.getDecision().equals("decline")) {
            postToModerate.setModerationStatus(ModerationStatus.DECLINED);
            postToModerate.setModeratorId(moderatorId);
            postRepository.save(postToModerate);
            return true;
        }
        if (request.getDecision().equals("accept")) {
            postToModerate.setModerationStatus(ModerationStatus.ACCEPTED);
            postToModerate.setModeratorId(moderatorId);
            postRepository.save(postToModerate);
            return true;
        }
        return false;
    }

    public StatResponse getStatResponse () {
        StatResponse statResponse = new StatResponse();
        List<Post> posts = postRepository.findAll();
        List<Vote> votes = voteRepository.findAll();
        int likesCount = (int) votes.stream()
                .filter(vote -> vote.getValue() == 1)
                .count();
        int dislikeCount = (int) votes.stream()
                .filter(vote -> vote.getValue() == -1)
                .count();
        int viewCount = posts.stream().mapToInt(Post::getViewCount).sum();
        statResponse.setViewsCount(viewCount);
        statResponse.setDislikesCount(dislikeCount);
        statResponse.setLikesCount(likesCount);
        statResponse.setPostsCount(posts.size());
        List<Date> dateList = new ArrayList<>();
        posts.forEach(post -> dateList.add(post.getTime()));
        dateList.sort(Comparator.naturalOrder());
        statResponse.setFirstPublication(dateList.get(0).getTime()/1000);
        return statResponse;
    }

    public boolean statisticsIsPublic () {
        if ( globalSettingRepository.findByCode(Code.STATISTICS_IS_PUBLIC).getValue().equals(Value.NO)) {
            return false;
        }
        return true;
    }

    public ResultResponse setVoteValue (PostVoteRequest request, Principal principal, int voteValue) {
        ResultResponse resultResponse = new ResultResponse();
        Post post = postRepository.getPostById(request.getPostId());
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        Vote vote = voteRepository.getVoteByUserAndPost(currentUser,post);
        if (vote != null) {
            if (vote.getValue() != voteValue) {
                vote.setValue(voteValue);
                vote.setTime(new Date());
                voteRepository.save(vote);
                resultResponse.setResult(true);
                return resultResponse;
            }
            resultResponse.setResult(false);
            return resultResponse;
        }
        Vote newVote = new Vote();
        newVote.setValue(voteValue);
        newVote.setUser(currentUser);
        newVote.setPost(post);
        newVote.setTime(new Date());
        voteRepository.save(newVote);
        resultResponse.setResult(true);
        return resultResponse;
    }

    public int getCountOfNewPosts () {
        return postRepository.getPostsByModerationStatus(ModerationStatus.NEW).size();
    }
}


