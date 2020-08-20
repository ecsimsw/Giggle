package com.giggle.Service;

import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Entity.CommunityType;
import com.giggle.Domain.Form.CreatePostForm;
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

    @Transactional
    public long createPost(CreatePostForm createPostForm){
        Post newPost = new Post();
        newPost.setCategory(createPostForm.getCategory());
        newPost.setCommunityType(CommunityType.valueOf(createPostForm.getCommunity()));
        newPost.setTitle(createPostForm.getTitle());
        newPost.setWriter("tester");
        newPost.setContent(createPostForm.getContent());
        newPost.setViewCnt(0);

        postRepository.save(newPost);
        return newPost.getId();
    }

    public List<Post> getAllPosts(){
        return postRepository.findAllPosts();
    }

    public List<Post> getPostsInCommunityCategory(CommunityType communityType, String categoryName){
        return postRepository.postInCommunityCategory(communityType, categoryName);
    }
}
