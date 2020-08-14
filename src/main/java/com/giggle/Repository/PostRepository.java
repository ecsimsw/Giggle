package com.giggle.Repository;

import com.giggle.Entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    public void save(Post post){
        em.persist(post);
    }
}
