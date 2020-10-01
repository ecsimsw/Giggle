package com.giggle.Service;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.ActivityForm;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;

    @Transactional
    public long createPost(PostForm postForm){
        long categoryId = Long.parseLong(postForm.getCategoryId());
        Category category = categoryService.findById(categoryId);

        Post newPost = new Post();
        newPost.setCategory(category);
        newPost.setTitle(postForm.getTitle());
        newPost.setWriter(postForm.getWriter());
        newPost.setContent(postForm.getContent().replace("\r\n", "<br>"));
        newPost.setViewCnt(0);

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
}
