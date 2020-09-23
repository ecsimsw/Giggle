package com.giggle.Service;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final MiddleCategoryService middleCategoryService;
    private final MainCategoryService mainCategoryService;

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
        categoryService.updatePostCnt(category, category.getPostCnt()+1);

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
        post.setContent(postForm.getContent().replace("\r\n", "<br>"));  // 한줄로 표시됨을 방지

        postRepository.updatePost(post);
    }

    @Transactional
    public void deletePost(Long id){
        Post post = findById(id);
        Category category = post.getCategory();
        categoryService.updatePostCnt(category, category.getPostCnt()-1);

        postRepository.remove(post);
    }
}
