package com.giggle.Service;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.ActivityForm;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Repository.LikeRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final LikeRepository likeRepository;

    @Transactional
    public long createPost(PostForm postForm, Member writer){
        long categoryId = Long.parseLong(postForm.getCategoryId());
        Category category = categoryService.findById(categoryId);

        Post newPost = new Post();
        newPost.setCategory(category);
        newPost.setTitle(postForm.getTitle());
        newPost.setContent(postForm.getContent().replace("\r\n", "<br>"));
        newPost.setLikeCnt(0);

        newPost.setWriterName(writer.getName());
        newPost.setWriter(writer.getLoginId());
        newPost.setProfileImg(writer.getProfileImg());

        categoryService.updatePostCnt(categoryId, category.getPostCnt()+1);
        postRepository.save(newPost);
        return newPost.getId();
    }

    public List<Post> getNewPost(int totalPostCnt, int newPostCnt){
        if(totalPostCnt-newPostCnt <= 0){return postRepository.getNewPost(0, newPostCnt); }
        else{ return postRepository.getNewPost(totalPostCnt-newPostCnt, newPostCnt); }
    }

    public Post readPost(Long id){
        return postRepository.findById(id);
    }

    public Post findById(Long id){return postRepository.findById(id);}

    @Transactional
    public void editPost(Long id, PostForm postForm){
        Post post = findById(id);
        long categoryId = Long.parseLong(postForm.getCategoryId());
        Category category = categoryService.findById(categoryId);
        post.setCategory(category);
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent().replace("\r\n", "<br>"));
    }

    @Transactional
    public void deletePost(Long id){
        Post post = findById(id);
        Category category = post.getCategory();
        categoryService.updatePostCnt(category.getId(), category.getPostCnt()-1);

        List<HotPost> hotPostList = likeRepository.getAllHotPost();

        for(HotPost hotPost : hotPostList) {
            if(hotPost.getPost().getId() == post.getId()){
                likeRepository.delete(hotPost);
                break;
            }
        }

        postRepository.remove(post);
    }

    public ActivityForm getActivityPost(String owner, int page, int postForPage){

        List<Post> postList = postRepository.getPostByOwner(owner);

        int totalCnt = postList.size();
        int from;
        int max;

        if((totalCnt-(page * postForPage))>=0){
            from = totalCnt-(page * postForPage);
            max = postForPage;
        }
        else{
            from = 0;
            max = totalCnt % postForPage;
        }

        List resultList = new ArrayList();

        for(int i=0; i<max; i++){
            resultList.add(postList.get(from+max-1-i));
        }

        ActivityForm resultTuple = new ActivityForm(resultList, totalCnt);

        return resultTuple;
    }

    public List<Post> getPostsInCategory(Category category, int page, int postForPage){
        int totalCnt = category.getPostCnt(); // 카테고리안 총 post 개수

        int from; // db에서 가져오는 시작 인덱스
        int postCnt; // db에서 가져오는 데이터의 개수의 최대치

        if((totalCnt-(page * postForPage))>=0){
            from = totalCnt-(page * postForPage);
            // 페이지가 작을수록 최신 데이터(큰 인덱스)부터 postForPage 개수 데이터를 가져옴

            postCnt = postForPage;
        }
        else{
            // 전체 Post 수가 한 페이지에 표시해야할 포스트보다 적은 경우
            from = 0;
            postCnt = totalCnt % postForPage;
        }

        return postRepository.postInCategory(category, from, postCnt);
    }

    public long checkLikeDuplicate(Post post, Member member){
        for(Like like : post.getLike()){
            if(like.getMember().getId() == member.getId()){
                return like.getId();
            }
        }
        return -1;
    }

    @Transactional
    public int likePost(Post post, Member member, int hotPostCnt){
        long likeId = checkLikeDuplicate(post, member);

        if(likeId != -1){
            //이미 좋아요.
            Like like = likeRepository.findById(likeId);
            post.getLike().remove(like);
            member.getLike().remove(like);
            likeRepository.delete(like);
            likeRepository.updatePostLikeCnt(post);
            return -1;
        }
        else{
            // 좋아요 등록
            Like like = new Like();
            like.setMember(member);
            like.setPost(post);
            likeRepository.save(like);

            post.getLike().add(like);
            member.getLike().add(like);

            likeRepository.updatePostLikeCnt(post);

            enrollHotPost(post, hotPostCnt);

            return 1;
        }
    }

    @Transactional
    public boolean enrollHotPost(Post post, int hotPostCnt){

        List<HotPost> hotPostList = likeRepository.getAllHotPost();

        if(hotPostList.size() < hotPostCnt){
            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getId() == post.getId()){
                    // 이미 hotPost에 등록된 post
                    return false;
                }
            }

            HotPost hotPost = new HotPost();
            hotPost.setPost(post);
            likeRepository.save(hotPost);

            Post postTemp = postRepository.findById(post.getId());
            postTemp.setHotPost(hotPost);

            return true;
        }
        else{
            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getId() == post.getId()){
                    // 이미 hotPost에 등록된 post
                    return false;
                }
            }

            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getLikeCnt() < post.getLikeCnt()){
                    // 기존 hotPost의 post보다 좋아요 숫자가 많을 경우,
                    likeRepository.delete(hotPost);

                    HotPost newHotPost = new HotPost();
                    newHotPost.setPost(post);
                    likeRepository.save(newHotPost);

                    return true;
                }
            }
        }

        return false;
    }

    public List<HotPost> getHotPostList(){
        List<HotPost> hotPosts = likeRepository.getAllHotPost();

        Collections.sort(hotPosts, new Comparator<HotPost>() {
            @Override
            public int compare(HotPost post1, HotPost post2) {
                if(post1.getPost().getLikeCnt() > post2.getPost().getLikeCnt()) {
                    return -1;
                }
                else{
                    return 1;
                }
            }
        });
        return hotPosts;
    }
}
