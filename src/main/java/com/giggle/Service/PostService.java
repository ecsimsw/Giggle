package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Entity.CommunityType;
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

    @Transactional
    public long createPost(CreatePostForm createPostForm){
        CommunityType communityType = CommunityType.valueOf(createPostForm.getCommunity());
        String categoryName = createPostForm.getCategory();

        Post newPost = new Post();
        newPost.setCategory(categoryName);
        newPost.setCommunityType(communityType);
        newPost.setTitle(createPostForm.getTitle());
        newPost.setWriter("tester");
        newPost.setContent(createPostForm.getContent());
        newPost.setViewCnt(0);

        Category category = categoryService.getCategoryByName(communityType, categoryName);
        categoryService.updatePostCnt(category, category.getPostCnt()+1);

        postRepository.save(newPost);
        return newPost.getId();
    }

    public List<Post> getAllPosts(){
        return postRepository.findAllPosts();
    }

    public List<Post> getPostsInCommunityCategory(CommunityType communityType, String categoryName){
        return postRepository.postInCommunityCategory(communityType, categoryName);
    }

    public List<Post> getPostsInCommunityCategory(CommunityType communityType, String categoryName, int page, int postForPage){
        return postRepository.postInCommunityCategory(communityType, categoryName,(page-1)*postForPage, (page*postForPage)-1);
    }
}
