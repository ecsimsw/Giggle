package com.giggle.Controller;

import com.giggle.Domain.Form.CreateCommentForm;
import com.giggle.Service.CommentService;
import com.giggle.Service.MemberService;
import com.giggle.Validator.CheckAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.giggle.Domain.Entity.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    @Autowired CheckAuthority checkAuthority;

    @PostMapping("/create")
    public String createComment(CreateCommentForm createCommentForm,
                                HttpSession httpSession) {

        String loginId = checkAuthority.checkLogin(httpSession);
        Member member = memberService.getByLoginId(loginId);

        commentService.createComment(createCommentForm,member);

        long postId=  Long.parseLong(createCommentForm.getPostId());

        return "redirect:/post/read?post="+postId;
    }

    @PostMapping("/edit")
    public String editComment(@RequestParam String comment, @RequestParam String content
                              ,HttpSession httpSession) {

        long commentId = Long.parseLong(comment);

        boolean isOwner = checkAuthority.checkOwner(httpSession, commentService.findById(commentId).getWriter());
        boolean isMaster = checkAuthority.checkMaster(httpSession);
        if(!isMaster && !isOwner){ throw new RuntimeException("You do not have access rights."); }

        commentService.editComment(commentId, content);

        long postId = commentService.findById(commentId).getPost().getId();
        return "redirect:/post/read?post="+postId;
    }

    @GetMapping("/delete")
    public String deleteComment(@RequestParam String comment,
                             HttpSession httpSession) {

        long commentId = Long.parseLong(comment);
        Comment commentToDelete = commentService.findById(commentId);

        boolean isOwner = checkAuthority.checkOwner(httpSession, commentToDelete.getWriter());
        boolean isMaster = checkAuthority.checkMaster(httpSession);
        if(!isMaster && !isOwner){ throw new RuntimeException("You do not have access rights."); }

        long postId = commentToDelete.getPost().getId();
        commentService.deleteComment(commentId);
        return "redirect:/post/read?post="+postId;
    }
}
