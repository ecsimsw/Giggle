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

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public String createComment(CreateCommentForm createCommentForm,
                                HttpSession httpSession) {

        if(httpSession.getAttribute("loginId")==null){
            throw new RuntimeException("Wrong access");
        }

        commentService.createComment(createCommentForm);
        long postId=  Long.parseLong(createCommentForm.getPostId());

        return "redirect:/post/read?post="+postId;
    }

    @PostMapping("/edit")
    public String editComment(@RequestParam String comment, @RequestParam String content
                              ,HttpSession httpSession) {

        long commentId = Long.parseLong(comment);

        String commentWriter = commentService.findById(commentId).getWriter();



        if(!httpSession.getAttribute("loginId").equals(commentWriter)){
            throw new RuntimeException("Wrong access");
        }

        commentService.editComment(commentId, content);

        long postId = commentService.findById(commentId).getPost().getId();
        return "redirect:/post/read?post="+postId;
    }

    @GetMapping("/delete")
    public String deleteComment(@RequestParam String comment,
                             HttpSession httpSession) {

        long commentId = Long.parseLong(comment);
        Comment commentToDelete = commentService.findById(commentId);

        if(!httpSession.getAttribute("loginId").equals(commentToDelete.getWriter())){
            throw new RuntimeException("Wrong access");
        }

        long postId = commentToDelete.getPost().getId();
        commentService.deleteComment(commentId);
        return "redirect:/post/read?post="+postId;
    }
}
