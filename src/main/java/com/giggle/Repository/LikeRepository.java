package com.giggle.Repository;

import com.giggle.Domain.Entity.HotPost;
import com.giggle.Domain.Entity.Like;
import com.giggle.Domain.Entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    public void save(HotPost hotPost){
        if(hotPost.getId() == null){
            em.persist(hotPost);
        }else{
            em.merge(hotPost);
        }
    }

    public Like findById(long id){
        return em.find(Like.class, id);
    }

    public void delete(Like like){
        em.remove(like);
    }

    public void delete(HotPost hotPost){ em.remove(hotPost); }

    public HotPost findHotPostById(long id){return em.find(HotPost.class, id);}

    public List<HotPost> getAllHotPost(){
        List<HotPost> hotPosts = em.createQuery("select h from HotPost h", HotPost.class).getResultList();
        return hotPosts;
    }

    public void updatePostLikeCnt(Post post){
        Post postInDB = em.find(Post.class, post.getId());
        postInDB.setLikeCnt(post.getLike().size());
    }

}
