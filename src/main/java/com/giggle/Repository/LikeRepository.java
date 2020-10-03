package com.giggle.Repository;

import com.giggle.Domain.Entity.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final EntityManager em;

    public void save(Like like){
        if(like.getId() == null){
            em.persist(like);
        }else{
            em.merge(like);
        }
    }

    public Like findById(long id){
        return em.find(Like.class, id);
    }

    public void delete(Like like){
        em.remove(like);
    }
}
