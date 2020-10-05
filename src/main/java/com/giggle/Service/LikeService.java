package com.giggle.Service;

import com.giggle.Domain.Entity.HotPost;
import com.giggle.Domain.Entity.Like;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.Post;
import com.giggle.Repository.LikeRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public long checkDuplicate(Post post, Member member){
        for(Like like : post.getLike()){
            if(like.getMember().getId() == member.getId()){
                return like.getId();
            }
        }
        return -1;
    }

    @Transactional
    public int likePost(Post post, Member member, int hotPostCnt){
        long likeId = checkDuplicate(post, member);

        if(likeId != -1){
            //이미 좋아요.
            Like like = likeRepository.findById(likeId);
            post.getLike().remove(like);
            member.getLike().remove(like);
            likeRepository.delete(like);
            likeRepository.updatePostLikeCnt(post);
            return -1;
        }
        else{
            // 좋아요 등록
            Like like = new Like();
            like.setMember(member);
            like.setPost(post);
            likeRepository.save(like);

            post.getLike().add(like);
            member.getLike().add(like);

            likeRepository.updatePostLikeCnt(post);

            enrollHotPost(post, hotPostCnt);

            return 1;
        }
    }

    @Transactional
    public boolean enrollHotPost(Post post, int hotPostCnt){

        List<HotPost> hotPostList = likeRepository.getAllHotPost();

        if(hotPostList.size() < hotPostCnt){
            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getId() == post.getId()){
                    // 이미 hotPost에 등록된 post
                    return false;
                }
            }

            HotPost hotPost = new HotPost();
            hotPost.setPost(post);
            likeRepository.save(hotPost);

            Post postTemp = postRepository.findById(post.getId());
            postTemp.setHotPost(hotPost);

            return true;
        }
        else{
            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getId() == post.getId()){
                    // 이미 hotPost에 등록된 post
                    return false;
                }
            }

            for(HotPost hotPost : hotPostList){
                if(hotPost.getPost().getLikeCnt() < post.getLikeCnt()){
                    // 기존 hotPost의 post보다 좋아요 숫자가 많을 경우,
                    likeRepository.delete(hotPost);

                    HotPost newHotPost = new HotPost();
                    newHotPost.setPost(post);
                    likeRepository.save(newHotPost);

                    return true;
                }
            }
        }

        return false;
    }

    public List<HotPost> getHotPostList(){
        List<HotPost> hotPosts = likeRepository.getAllHotPost();

        Collections.sort(hotPosts, new Comparator<HotPost>() {
            @Override
            public int compare(HotPost post1, HotPost post2) {
                if(post1.getPost().getLikeCnt() > post2.getPost().getLikeCnt()) {
                    return -1;
                }
                else{
                    return 1;
                }
            }
        });
        return hotPosts;
    }
}
