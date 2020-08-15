package com.giggle.Repository;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.Post;
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
 }
