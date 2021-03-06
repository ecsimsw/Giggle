package com.giggle.Repository;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.PostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    public void save(Post post){
        if(post.getId()==null){
            em.persist(post);
        }else{
            em.merge(post);
        }
    }

    public List<Post> postInCategory(Category category, int from, int postCnt){
        List<Post> selectedPosts = em.createQuery("select p from Post p where p.category =:category",Post.class)
                .setParameter("category", category)
                .setFirstResult(from)
                .setMaxResults(postCnt)
                .getResultList();

        Collections.reverse(selectedPosts);
        return selectedPosts;
    }

    public List<Post> getNewPost(int from, int postCnt){
        List<Post> selectedPosts = em.createQuery("select p from Post p",Post.class)
                .setMaxResults(postCnt)
                .setFirstResult(from)
                .getResultList();

        Collections.reverse(selectedPosts);
        return selectedPosts;
    }

    public Post findById(long id){
        return em.find(Post.class, id);
    }

    public void remove(Post post){ em.remove(post); }

    public List<Post> getPostByOwner(String writer){
        List<Post> selectedPosts = em.createQuery("select p from Post p where p.writer =:writer",Post.class)
                .setParameter("writer", writer)
                .getResultList();
        return selectedPosts;
    }
 }
