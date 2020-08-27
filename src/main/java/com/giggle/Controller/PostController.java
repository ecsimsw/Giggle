package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.CreatePostForm;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MainCategoryService;
import com.giggle.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final MainCategoryService mainCategoryService;

    @GetMapping("/create")
    public String createPost(@RequestParam("category") Long categoryId, Model model) {
        Category category = categoryService.findById(categoryId);
        MiddleCategory middleCategory = category.getMiddleCategory();

        model.addAttribute("category", category);
        model.addAttribute("middleCategory", middleCategory);
        return "createPostForm";
    }

    @PostMapping("/create")
    public String createPost(CreatePostForm createPostForm, Model model) {
        postService.createPost(createPostForm);

        Long categoryId = Long.parseLong(createPostForm.getCategoryId());
        return "redirect:/post/board?category="+categoryId+"&page=1";
    }

    @GetMapping("/board")
    public String board(@RequestParam("category") Long categoryId,
                        @RequestParam int page,
                        Model model) {

        // side bar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);


        // categories

        Category category = categoryService.findById(categoryId);

        MiddleCategory middleCategory = category.getMiddleCategory();
        model.addAttribute("middleCategory", middleCategory);


        // pagination

        int visiblePages = 10;

        model.addAttribute("visiblePages", visiblePages);
        model.addAttribute("totalPost", category.getPostCnt());
        model.addAttribute("pageNow", page);


        // print posts

        int postForPage =15;
        model.addAttribute("postForPage", postForPage);
        model.addAttribute("category", category);
        List<Post> postList = categoryService.getPostsInCategory(category, page, postForPage);

        model.addAttribute("postList", postList);

        return "board";
    }

    @GetMapping("/read")
    public String readPost(@RequestParam Long post, Model model){

        // side bar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        // post

        Post postToRead = postService.readPost(post);
        model.addAttribute("post", postToRead);

        return "postRead";
    }

}
