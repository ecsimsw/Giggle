package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.CommunityType;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreatePostForm;
import com.giggle.Service.CategoryService;
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
    private final ObjectMapper objectMapper;

    @GetMapping("/create/{communityName}/{categoryName}")
    public String create(@PathVariable String communityName, @PathVariable String categoryName, Model model){
        List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(CommunityType.valueOf(communityName));
        model.addAttribute("communityName",communityName);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("categoryNameList", categoryNameList);
        return "createPostForm";
    }

    @GetMapping("/create/{communityName}")
    public String createPost(@PathVariable String communityName, Model model){
        List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(CommunityType.valueOf(communityName));
        model.addAttribute("communityName",communityName);
        model.addAttribute("categoryName", null);
        model.addAttribute("categoryNameList", categoryNameList);
        return "createPostForm";
    }


    @PostMapping("/create/{communityName}/{categoryName}")
    public String createNewPost(@PathVariable String communityName,
                                @PathVariable String categoryName,
                                CreatePostForm createPostForm) {

        postService.createPost(createPostForm);
        return "redirect:/post/board/"+communityName+"/"+categoryName;
    }

//    @GetMapping("/board/{communityName}/{categoryName}")
//    public String postList(@PathVariable String communityName, @PathVariable String categoryName,
//                           HttpSession session, Model model){
//        CommunityType eCommunityType = CommunityType.valueOf(communityName);
//        List<String> categoryNames = categoryService.getCategoryNamesInCommunity(eCommunityType);
//        List<Post> posts = null;
//
//        if(categoryName.equals("All")){ posts = postService.getAllPosts(); }
//        else if(categoryNames.contains(categoryName)){
//            posts = postService.getPostsInCommunityCategory(CommunityType.valueOf(communityName), categoryName);
//        }
//        else{ throw new RuntimeException("non-existent category"); }
//
//        if(posts.isEmpty()){}
//        else { Collections.reverse(posts); }
//
//        model.addAttribute("post",posts);
//        model.addAttribute("communityName", communityName);
//        model.addAttribute("categoryName", categoryName);
//
//        String loginId = (String)session.getAttribute("loginId");
//
//        List<String> communityNameList = new ArrayList<>();
//        List<List<String>> communityList = new ArrayList<>();
//
//        for(CommunityType community : CommunityType.values()){
//            communityNameList.add(community.name());
//            List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(community);
//            communityList.add(categoryNameList);
//        }
//
//        model.addAttribute("communityNameList", communityNameList);
//        model.addAttribute("communityList", communityList);
//        model.addAttribute("loginId",loginId);
//
//        List<String> categoriesInCommunity = categoryService.getCategoryNamesInCommunity(eCommunityType);
//        model.addAttribute("categoriesInCommunity",categoriesInCommunity);
//
//        return "board";
//    }

    @GetMapping("/board/{communityName}/{categoryName}")
    public String postList(@PathVariable String communityName, @PathVariable String categoryName,
                           @RequestParam int page,
                           HttpSession session, Model model){

        // top bar
        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);


        // side bar
        CommunityType eCommunityType = CommunityType.valueOf(communityName);

        List<String> communityNameList = new ArrayList<>();
        List<List<String>> communityList = new ArrayList<>();

        for(CommunityType community : CommunityType.values()){
            communityNameList.add(community.name());
            List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(community);
            communityList.add(categoryNameList);
        }

        model.addAttribute("communityNameList", communityNameList);
        model.addAttribute("communityList", communityList);


        // category board
        List<String> categoriesInCommunity = categoryService.getCategoryNamesInCommunity(eCommunityType);
        model.addAttribute("categoriesInCommunity",categoriesInCommunity);


        // post box
        List<Post> posts = null;
        int postForPage = 20;

        if(categoryName.equals("All")){ posts = postService.getAllPosts();} // need pagination
        else if(categoryService.getCategoryNamesInCommunity(eCommunityType).contains(categoryName)){
            posts = postService.getPostsInCommunityCategory(CommunityType.valueOf(communityName), categoryName, page, postForPage);
        }
        else{ throw new RuntimeException("non-existent category"); }

        if(!posts.isEmpty()){ Collections.reverse(posts); }

        model.addAttribute("post", posts);
        model.addAttribute("communityName", communityName);
        model.addAttribute("categoryName", categoryName);


        // pagination

        model.addAttribute("postForPage", postForPage);
        model.addAttribute("totalPost", categoryService.getTotalCnt(communityName,categoryName));


        return "board";
    }



}
