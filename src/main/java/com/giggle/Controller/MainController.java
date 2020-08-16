package com.giggle.Controller;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.Post;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MemberService;
import com.giggle.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final CategoryService categoryService;

    @RequestMapping("/")
    @ResponseBody
    public String hello(){ return "hi"; }

    @RequestMapping("/board")
    @ResponseBody
    public String board(@SessionAttribute String loginId){
        return loginId;
    }

    @GetMapping("/post/{categoryName}")
    public String postList(@PathVariable String categoryName, Model model){
        List<String> categoryNames = categoryService.getCategoryNames();
        List<Post> posts = null;

        if(categoryName.equals("All")){
            posts = postService.getAllPosts();
        }
        else if(categoryNames.contains(categoryName)){
            posts = postService.getPostsInCategory(categoryName);
        }
        else{
            throw new RuntimeException();
        }

        if(posts.isEmpty()){}
        else { Collections.reverse(posts); }

        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("postList", posts);
        model.addAttribute("category", categoryName);
        return "board";
    }
}
