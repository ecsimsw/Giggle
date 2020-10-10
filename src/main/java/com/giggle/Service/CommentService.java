package com.giggle.Service;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.ActivityForm;
import com.giggle.Domain.Form.CreateCommentForm;
import com.giggle.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.giggle.Domain.Entity.Comment;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(CreateCommentForm createCommentForm, Post post, Member writer){
        Comment newComment = new Comment();

        if(createCommentForm.getCommentId()== null){
            newComment.setLevel(1);
        }

        else{
            long supCommentId = Long.parseLong(createCommentForm.getCommentId());
            Comment supComment = commentRepository.findById(supCommentId);

            if(!supComment.isLive()){
                throw new RuntimeException("SuperComment is already dead");
            }
            newComment.setLevel(supComment.getLevel()+1);
            newComment.setSuperComment(supComment);
            supComment.getSubComment().add(newComment);
        }

        newComment.setContent(createCommentForm.getContent().replace("\r\n", "<br>"));
        newComment.setPost(post);

        newComment.setWriterName(writer.getName());
        newComment.setWriter(writer.getLoginId());
        newComment.setProfileImg(writer.getProfileImg());

        newComment.setLive(true);
        commentRepository.save(newComment);
    }

    @Transactional
    public void deleteComment(long commentId){

        Comment commentToDelete = commentRepository.findById(commentId);

        if(commentToDelete.getSubComment().size()==0){
            while(commentToDelete!=null){
                Comment superComment = commentToDelete.getSuperComment();
                if(superComment ==null){
                    commentRepository.deleteById(commentToDelete.getId());
                    break;
                }
                superComment.getSubComment().remove(commentToDelete);
                commentRepository.deleteById(commentToDelete.getId());
                if(superComment.getSubComment().size()==0 && !superComment.isLive()){
                    commentToDelete = superComment;
                }
                else{ break; }
            }
        }
        else if(commentToDelete!=null){
            commentToDelete.setContent("삭제된 댓글입니다.");
            commentToDelete.setLive(false);
        }
    }

    public Comment findById(long id){
        return commentRepository.findById(id);
    }

    @Transactional
    public void editComment(long id, String content){
        Comment comment = commentRepository.findById(id);
        comment.setContent(content);
    }

    public ActivityForm getActivityComment(String owner, int page, int postForPage){
        List<Comment> commentList = commentRepository.getCommentByOwner(owner);

        int totalCnt = commentList.size();
        int from;
        int max;

        if((totalCnt-(page * postForPage))>=0){
            from = totalCnt-(page * postForPage);
            max = postForPage;
        }
        else{
            from = 0;
            max = totalCnt % postForPage;
        }

        List resultList = new ArrayList();

        for(int i=0; i<max; i++){
            resultList.add(commentList.get(from+max-i-1));
        }

        ActivityForm resultTuple = new ActivityForm(resultList, totalCnt);

        return resultTuple;
    }
}
