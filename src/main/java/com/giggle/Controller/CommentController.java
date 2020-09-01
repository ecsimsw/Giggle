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

import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MainCategoryService mainCategoryService;
    private final PostService postService;

    @PostMapping("/create")
    public String createComment(CreateCommentForm createCommentForm, Model model){
        commentService.createComment(createCommentForm);


        // side bar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        // post
        long postId=  Long.parseLong(createCommentForm.getPostId());
        model.addAttribute("post", postService.findById(postId));

        return "postRead";
    }
}
