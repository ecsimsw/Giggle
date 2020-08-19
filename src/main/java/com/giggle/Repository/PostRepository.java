package com.giggle.Repository;

import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Entity.CommunityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    public void save(Post post){
        em.persist(post);
    }

    public List<Post> findAllPosts(){
        List<Post> selectedPosts = em.createQuery("select p from Post p", Post.class)
                .getResultList();
        return selectedPosts;
    }

    public List<Post> postInCommunityCategory(CommunityType type, String categoryName){
        List<Post> selectedPosts = em.createQuery("select p from Post p where p.postType =:postType AND p.category = :categoryName",Post.class)
                .setParameter("postType", type.name())
                .setParameter("categoryName",categoryName)
                .getResultList();
        return selectedPosts;
    }
 }
