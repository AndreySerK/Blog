package main.service;

import lombok.RequiredArgsConstructor;
import main.DTO.*;
import main.api.request.AddCommentRequest;
import main.api.request.NewPostRequest;
import main.api.request.PostModerateRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.mappers.PostMapper;
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
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MIN_TEXT_LENGTH = 50;
    public static final int ANNOUNCE_MAX_LENGTH = 150;

    private final PostRepository postRepository;
    private final GlobalSettingRepository globalSettingRepository;
    private final PostCommentRepository postCommentRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final MapperService mapperService;


    private List<Post> getPosts(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent();
    }

    private List<Post> getPostsByModeratorId(int offset, int limit, String email) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return postRepository.findAll(nextPage).getContent()
                .stream()
                .filter(post -> Objects.equals(post.getModeratorId(), getCurrentUserIdByEmail(email))
                        || post.getModerationStatus().equals(ModerationStatus.NEW))
                .collect(Collectors.toList());
    }

    private Integer getCurrentUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("not found")).getId();
    }

    private List<Post> getPostsByDate(int offset, int limit, String date) {

        return getPosts(offset, limit)
                .stream()
                .filter(post -> post.getTime().toString().contains(date))
                .collect(Collectors.toList());
    }

    private List<Post> getPostsByTag(int offset, int limit, String tag) {
        List<Post> posts = new ArrayList<>();
        getPosts(offset, limit)
                .forEach(post -> {
                    post.getTags()
                            .forEach(t -> {
                                if (t.getName().contains(tag)) {
                                    posts.add(post);
                                }
                            });
                });
        return posts;
    }

    private List<Post> getActiveAcceptedPostsBeforeCurrentTime() {
        return postRepository
                .findAll()
                .stream()
                .filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                        && post.getIsActive() == 1
                        && post.getTime().before(new Date()))
                .collect(Collectors.toList());
    }

    public Post getActiveAcceptedPostByIdBeforeCurrentTime(Integer id) {
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

    public void addViewToPost(Integer id) {
        Post post = getActiveAcceptedPostByIdBeforeCurrentTime(id);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    public PostByIdDto getPostById(Integer id, Principal principal) {
        Post post = getActiveAcceptedPostByIdBeforeCurrentTime(id);
        if (post == null) {
            return null;
        }
        int view;
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            if (user.getIsModerator() == 0 || user.getId() != post.getUser().getId()) {
                view = post.getViewCount() + 1;
                post.setViewCount(view);
                postRepository.save(post);
            }
        } else {
            view = post.getViewCount() + 1;
            post.setViewCount(view);
            postRepository.save(post);
        }
        return mapperService.convertToPostByIdDto(post);
    }

    public List<Integer> getYearsOfPosts() {
        Set<Integer> yearsSet = new HashSet<>();
        getActiveAcceptedPostsBeforeCurrentTime()
                .forEach(post -> {
                    yearsSet.add(post
                            .getTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate().getYear());
                });
        List<Integer> years = new ArrayList<>(yearsSet);
        Collections.sort(years);
        return years;
    }

    public Map<String, Integer> getPostsCountOnDate(Integer year) {
        Map<String, Integer> postsMap = new HashMap<>();
        List<Post> postsOfYear = new ArrayList<>();
        Set<String> days = new HashSet<>();
        getActiveAcceptedPostsBeforeCurrentTime()
                .stream()
                .filter(post -> post.getTime().toString().contains(year.toString()))
                .forEach(post -> {
                    days.add(post.getTime().toString().substring(0, 10));
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

    public Integer getCountOfAllPosts() {
        return postRepository.getCountOfAllPosts();
    }

    public Integer getCountOfPostsWithQuery(String query) {
        List<Post> postsWithQuery = new ArrayList<>();
        List<Post> posts = postRepository.findAll();
        posts.forEach(post -> {
            if (post.getText().contains(query)) {
                postsWithQuery.add(post);
            }
        });
        return postsWithQuery.size();
    }

    private List<PostDto> getListOfPostDto(List<Post> posts, String moderationStatus, int isActive,
                                           Date date) {
        List<PostDto> postDtoList = new ArrayList<>();
        posts.stream()
                .filter(post -> post.getModerationStatus().toString().toLowerCase().equals(moderationStatus)
                        && post.getIsActive() == isActive
                        && post.getTime().before(date))
                .forEach(post -> {
                    PostDto postDto = PostMapper.INSTANCE.postToPostDto(post);
                    UserForPostDto user = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
                    postDto.setUser(user);
                    postDto.setTimestamp(post.getTime().getTime() / 1000);
                    setPostDto(postDtoList, post, postDto);
                });
        return postDtoList;
    }

    private void setPostDto(List<PostDto> postDtoList, Post post, PostDto postDto) {
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
        String announce = post.getText()
                .replaceAll("<(/)?([0-9A-Za-z\\-;:./=\"\\s]+)?>", "")
                .replaceAll("&nbsp;", "");
        if (announce.length() < ANNOUNCE_MAX_LENGTH) {
            postDto.setAnnounce(announce);
        } else {
            postDto.setAnnounce(announce.substring(0, 149) + "...");
        }
        postDtoList.add(postDto);
    }

    public List<Post> getListOfPostByUserId(int offset, int limit, Principal principal) {
        int currentUserId = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found")).getId();
        return getPosts(offset, limit)
                .stream()
                .filter(post -> post.getUserId() == currentUserId)
                .collect(Collectors.toList());
    }

    private List<PostDto> postToPostDtoByUserId(List<Post> posts) {
        List<PostDto> postDtoList = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDto = PostMapper.INSTANCE.postToPostDto(post);
            UserForPostDto user = UserMapper.INSTANCE.userToUserForPostDto(post.getUser());
            postDto.setUser(user);
            postDto.setTimestamp(post.getTime().getTime() / 1000);
            setPostDto(postDtoList, post, postDto);
        });
        return postDtoList;
    }

    public List<PostDto> getListOfPostDtoByUserId(List<Post> posts, int isActive) {
        return postToPostDtoByUserId(posts.stream()
                .filter(post -> post.getIsActive() == isActive)
                .collect(Collectors.toList()));
    }

    public List<PostDto> getListOfPostDtoByUserId(List<Post> posts, int isActive,
                                                  String moderationStatus) {
        return postToPostDtoByUserId(posts.stream()
                .filter(post -> post.getIsActive() == isActive
                        && post.getModerationStatus().toString().toLowerCase().equals(moderationStatus))
                .collect(Collectors.toList()));
    }

    public List<PostDto> getListOfPostDtoWithQuery(int offset, int limit, String query) {
        List<Post> postsWithQuery = getPosts(offset, limit)
                .stream()
                .filter(post -> post.getText().contains(query))
                .collect(Collectors.toList());
        return getListOfPostDto(postsWithQuery, "accepted", 1, new Date());
    }

    public List<PostDto> getSortedListOfPostDtoByMode(int offset, int limit, String mode) {
        List<Post> postList = getPosts(offset, limit);
        List<PostDto> postDtoList = getListOfPostDto(postList, "accepted", 1, new Date());
        switch (mode) {
            case "early":
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp));
                return postDtoList;
            case "popular":
                postDtoList.sort(Comparator.comparingInt(PostDto::getCommentCount).reversed());
                return postDtoList;
            case "best":
                postDtoList.sort(Comparator.comparingInt(PostDto::getLikeCount).reversed());
                return postDtoList;
            default:
                postDtoList.sort(Comparator.comparingLong(PostDto::getTimestamp).reversed());
                return postDtoList;
        }
    }

    public List<PostDto> getListOfPostDtoByDate(int offset, int limit, String date) {
        return getListOfPostDto(getPostsByDate(offset, limit, date), "accepted", 1, new Date());
    }

    public List<PostDto> getListOfPostDtoByModerationStatus(int offset, int limit, String status) {
        return getListOfPostDto(getPosts(offset, limit), status, 1, new Date());
    }

    public List<PostDto> getListOfPostDtoByModerationStatusAndId(int offset, int limit,
                                                                 String status) {

        return getListOfPostDto(getPostsByModeratorId(offset, limit, getCurrentUserEmail()), status, 1,
                new Date());
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public List<PostDto> getListOfPostDtoByTag(int offset, int limit, String tag) {
        return getListOfPostDto(getPostsByTag(offset, limit, tag), "accepted", 1, new Date());
    }

    public HashMap<String, String> getErrors(NewPostRequest postRequest) {
        HashMap<String, String> errors = new HashMap<>();
        if (postRequest.getTitle().length() < MIN_TITLE_LENGTH) {
            errors.put("title", "Заголовок не установлен");
        }
        if (postRequest.getText().length() < MIN_TEXT_LENGTH) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        return errors;
    }

    private List<Tag> getTagsByName(List<String> tagNames) {
        tagNames.forEach(t -> {
                    if (tagRepository.findTagByName(t).isEmpty()) {
                        Tag newTag = new Tag();
                        newTag.setName(t);
                        tagRepository.save(newTag);
                    }
                });
        return tagNames.stream()
                .map(t -> tagRepository.findTagByName(t)
                        .orElseThrow(NoSuchElementException::new))
                .collect(Collectors.toList());
    }

    public NewPostResponse addNewPost(NewPostRequest newPostRequest, Principal principal) {
        NewPostResponse postsResponse = new NewPostResponse();
        Map<String, String> errors = getErrors(newPostRequest);
        if (errors.isEmpty()) {
            Post newPost = new Post();
            Date postDate = new Date(newPostRequest.getTimestamp() * 1000);
            newPost.setTime(postDate.compareTo(new Date()) <= 0 ? new Date() : postDate);
            newPost.setIsActive(newPostRequest.getActive());
            boolean isModeration = globalSettingRepository
                    .findByCode(Code.POST_PREMODERATION).getValue().equals(Value.NO);
            if (isModeration && newPost.getIsActive() == 1) {
                newPost.setModerationStatus(ModerationStatus.ACCEPTED);
            } else {
                newPost.setModerationStatus(ModerationStatus.NEW);
            }
            newPost.setTitle(newPostRequest.getTitle());
            newPost.setText(newPostRequest.getText());
            newPost.setUser(userRepository.findByEmail(principal.getName()).orElseThrow());
            newPost.setViewCount(0);
            newPost.setTags(getTagsByName(newPostRequest.getTags()));
            postRepository.save(newPost);
            postsResponse.setResult(true);
            return postsResponse;
        }
        postsResponse.setResult(false);
        postsResponse.setErrors(errors);
        return postsResponse;
    }

    public NewPostResponse changePost(NewPostRequest newPostRequest, Principal principal, int id) {
        NewPostResponse postResponse = new NewPostResponse();
        Map<String, String> errors = getErrors(newPostRequest);
        if (errors.isEmpty()) {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new FindException("Пост с таким id не найден"));
            Date postDate = new Date(newPostRequest.getTimestamp() * 1000);
            post.setTime(postDate.compareTo(new Date()) <= 0 ? new Date() : postDate);
            post.setIsActive(newPostRequest.getActive());
            post.setTitle(newPostRequest.getTitle());
            post.setText(newPostRequest.getText());
            post.setTags(getTagsByName(newPostRequest.getTags()));
            if (userRepository.findByEmail(principal.getName()).get().getIsModerator() == 0) {
                post.setModerationStatus(ModerationStatus.NEW);
            }
            postRepository.save(post);
            postResponse.setResult(true);
            return postResponse;
        }
        postResponse.setResult(false);
        postResponse.setErrors(errors);
        return postResponse;
    }

    public String isCommentAddSuccess(AddCommentRequest request) {

        if (request.getText() == null || request.getText().length() < 5) {
            return "Текст комментария не задан или слишком короткий";
        }
        if (request.getParentId() == null) {
            if (!postRepository.existsById(request.getPostId())) {
                return "BAD_REQUEST";
            }
        } else if (!postRepository.existsById(request.getPostId())
                || !postCommentRepository.existsById(request.getParentId())) {
            return "BAD_REQUEST";
        }
        return "OK";
    }

    public AddCommentResponse getAddCommentResponse(AddCommentRequest request, Principal principal) {
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

    public AddCommentResponseErr getAddCommentErr(AddCommentRequest request) {
        AddCommentErr addCommentErr = new AddCommentErr();
        AddCommentResponseErr addCommentResponseErr = new AddCommentResponseErr();
        addCommentErr.setText(isCommentAddSuccess(request));
        addCommentResponseErr.setResult(false);
        addCommentResponseErr.setErrors(addCommentErr);
        return addCommentResponseErr;
    }

    public boolean isPostModerateSuccess(PostModerateRequest request, Principal principal) {
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

    public StatResponse getStatResponse() {
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
        return setStatResponse(statResponse, posts);
    }

    static StatResponse setStatResponse(StatResponse statResponse, List<Post> posts) {
        List<Date> dateList = new ArrayList<>();
        posts.forEach(post -> dateList.add(post.getTime()));
        dateList.sort(Comparator.naturalOrder());
        statResponse.setFirstPublication(dateList.get(0).getTime() / 1000);
        return statResponse;
    }

    public boolean statisticsIsPublic() {
        return !globalSettingRepository
                .findByCode(Code.STATISTICS_IS_PUBLIC)
                .getValue()
                .equals(Value.NO);
    }

    public ResultResponse setVoteValue(PostVoteRequest request, Principal principal, int voteValue) {
        ResultResponse resultResponse = new ResultResponse();
        Post post = postRepository.getPostById(request.getPostId());
        User currentUser = userRepository.findByEmail(principal.getName()).orElseThrow();
        Vote vote = voteRepository.getVoteByUserAndPost(currentUser, post);
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

    public int getCountOfNewPosts() {
        return postRepository.getPostsByModerationStatus(ModerationStatus.NEW).size();
    }
}


