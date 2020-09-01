package com.giggle.Service;

import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreateCommentForm;
import com.giggle.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.giggle.Domain.Entity.Comment;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public void createComment(CreateCommentForm createCommentForm){
        Comment newComment = new Comment();
        long postId = Long.parseLong(createCommentForm.getPostId());
        Post post = postService.findById(postId);

        if(createCommentForm.getCommentId()== null){
            newComment.setLevel(1);
        }

        else{
            long supCommentId = Long.parseLong(createCommentForm.getCommentId());
            Comment supComment = commentRepository.findById(supCommentId);

            log.info("supCommentId = "+ supCommentId);

            newComment.setLevel(supComment.getLevel()+1);
            newComment.setSuperComment(supComment);
            supComment.getSubComment().add(newComment);
        }

        newComment.setContent(createCommentForm.getContent());
        newComment.setPost(post);
        newComment.setWriter("Tester");

        commentRepository.save(newComment);
    }
}
