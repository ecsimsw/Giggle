package com.giggle.Service;

import com.giggle.Domain.Entity.Like;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.Post;
import com.giggle.Repository.LikeRepository;
import com.giggle.Repository.MemberRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    public long checkDuplicate(Post post, Member member){
        for(Like like : post.getLike()){
            if(like.getMember().getId() == member.getId()){
                return like.getId();
            }
        }
        return -1;
    }

    @Transactional
    public int likePost(Post post, Member member){
        long likeId = checkDuplicate(post, member);

        if(likeId != -1){
            //이미 좋아요.
            Like like = likeRepository.findById(likeId);
            post.getLike().remove(like);
            member.getLike().remove(like);
            likeRepository.delete(like);
           return -1;
        }
        else{
            Like like = new Like();
            like.setMember(member);
            like.setPost(post);
            likeRepository.save(like);

            post.getLike().add(like);
            member.getLike().add(like);
            return 1;
        }
    }
}
