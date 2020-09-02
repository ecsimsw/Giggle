package com.giggle.Controller;

import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreateCommentForm;
import com.giggle.Service.CommentService;
import com.giggle.Service.MainCategoryService;
import com.giggle.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.giggle.Domain.Entity.*;

import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MainCategoryService mainCategoryService;
    private final PostService postService;

    @PostMapping("/create")
    public String createComment(CreateCommentForm createCommentForm){
        commentService.createComment(createCommentForm);
        long postId=  Long.parseLong(createCommentForm.getPostId());

        return "redirect:/post/read?post="+postId;
    }

    @PostMapping("/edit")
    public String editComment(@RequestParam String comment, @RequestParam String content) {
        long commentId = Long.parseLong(comment);
        commentService.editComment(commentId, content);

        long postId = commentService.findById(commentId).getPost().getId();
        return "redirect:/post/read?post="+postId;
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam String comment) {
        long commentId = Long.parseLong(comment);
        Comment commentToDelete = commentService.findById(commentId);
        long postId = commentToDelete.getPost().getId();
        commentService.deleteComment(commentId);
        return "redirect:/post/read?post="+postId;
    }
}
