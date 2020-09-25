package com.giggle.Repository;

import com.giggle.Domain.Entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public Comment findById(long id){
        return em.find(Comment.class, id);
    }

    public void save(Comment comment){
        if(comment.getId()== null){
            em.persist(comment);
        }
        else{
            em.merge(comment);
        }
    }

    public void deleteById(long id){
        em.remove(findById(id));
    }
}
