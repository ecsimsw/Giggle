package com.giggle.Service;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.CreatePostForm;
import com.giggle.Repository.CategoryRepository;
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
    public long createPost(CreatePostForm createPostForm){
        long categoryId = Long.parseLong(createPostForm.getCategoryId());
        Category category = categoryService.findById(categoryId);

        Post newPost = new Post();
        newPost.setCategory(category);
        newPost.setTitle(createPostForm.getTitle());
        newPost.setWriter("tester");
        newPost.setContent(createPostForm.getContent());
        newPost.setViewCnt(0);
        categoryService.updatePostCnt(category, category.getPostCnt()+1);

        postRepository.save(newPost);

        MiddleCategory middleCategory = category.getMiddleCategory();
        middleCategoryService.updatePostCnt(middleCategory,middleCategory.getPostCnt()+1);

        MainCategory mainCategory = middleCategory.getMainCategory();
        mainCategoryService.updatePostCnt(mainCategory, mainCategory.getPostCnt()+1);

        return newPost.getId();
    }

    public List<Post> getNewPost(int totalPostCnt, int newPostCnt){
        if(totalPostCnt-newPostCnt <= 0){
            return postRepository.getNewPost(0, newPostCnt);
        }
        else{
            return postRepository.getNewPost(totalPostCnt-newPostCnt, newPostCnt);
        }
    }
}
