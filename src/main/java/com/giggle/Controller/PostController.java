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

    @GetMapping("/create/{communityType}/{category}")
    public String create(@PathVariable String communityType, @PathVariable String category, Model model){
        List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(CommunityType.valueOf(communityType));
        model.addAttribute("communityType",communityType);
        model.addAttribute("categoryNow", category);
        model.addAttribute("categoryNameList", categoryNameList);
        return "createPostForm";
    }

    @GetMapping("/create/{communityType}")
    public String createPost(@PathVariable String communityType, Model model){
        List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(CommunityType.valueOf(communityType));
        model.addAttribute("communityType",communityType);
        model.addAttribute("categoryNow", null);
        model.addAttribute("categoryNameList", categoryNameList);
        return "createPostForm";
    }


    @PostMapping("/create")
    public String createNewPost(CreatePostForm createPostForm) throws JsonProcessingException {
        String result = objectMapper.writeValueAsString(createPostForm);
        Post newPost = new Post();
        newPost.setCategory(createPostForm.getCategory());
        newPost.setCommunityType(CommunityType.valueOf(createPostForm.getCommunity()));
        newPost.setTitle(createPostForm.getTitle());
        newPost.setWriter("tester");
        newPost.setContent(createPostForm.getContent());

        postService.createPost(newPost);
        return "redirect:/post/"+createPostForm.getCategory();
    }

    @GetMapping("/board/{communityName}/{categoryName}")
    public String postList(@PathVariable String communityName, @PathVariable String categoryName, HttpSession session, Model model){

        CommunityType eCommunityType = CommunityType.valueOf(communityName);
        List<String> categoryNames = categoryService.getCategoryNamesInCommunity(eCommunityType);
        List<Post> posts = null;

        if(categoryName.equals("All")){
            posts = postService.getAllPosts();
        }
        else if(categoryNames.contains(categoryName)){
            posts = postService.getPostsInCommunityCategory(CommunityType.valueOf(communityName), categoryName);
        }
        else{
            throw new RuntimeException("non-existent category");
        }

        if(posts.isEmpty()){}
        else {
            Collections.reverse(posts);
        }

        model.addAttribute("post",posts);
        model.addAttribute("communityName", communityName);
        model.addAttribute("categoryName", categoryName);

        String loginId = (String)session.getAttribute("loginId");

        List<String> communityNameList = new ArrayList<>();
        List<List<String>> communityList = new ArrayList<>();

        for(CommunityType community : CommunityType.values()){
            communityNameList.add(community.name());
            List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(community);
            communityList.add(categoryNameList);
        }

        model.addAttribute("communityNameList", communityNameList);
        model.addAttribute("communityList", communityList);
        model.addAttribute("loginId",loginId);


        List<String> categoriesInCommunity = categoryService.getCategoryNamesInCommunity(eCommunityType);
        model.addAttribute("categoriesInCommunity",categoriesInCommunity);

        return "board";
    }
}
