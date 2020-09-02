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
        em.persist(comment);
    }

    public void deleteById(long id){
        em.remove(findById(id));
    }

    public void editComment(Comment comment){
        em.merge(comment);
    }
}
