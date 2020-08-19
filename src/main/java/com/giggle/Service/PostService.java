package com.giggle.Service;

import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Entity.CommunityType;
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
    public long createPost(Post post){
        postRepository.save(post);
        return post.getId();
    }

    public List<Post> getAllPosts(){
        return postRepository.findAllPosts();
    }

    public List<Post> getPostsInCommunityCategory(CommunityType communityType, String categoryName){
        return postRepository.postInCommunityCategory(communityType, categoryName);
    }
}
