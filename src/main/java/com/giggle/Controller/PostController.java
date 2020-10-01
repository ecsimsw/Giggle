package com.giggle.Controller;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.ActivityForm;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Service.CategoryService;
import com.giggle.Service.CommentService;
import com.giggle.Service.PostService;
import com.giggle.Validator.CheckAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final CategoryService categoryService;

    @Autowired CheckAuthority checkAuthority;

    private final int visiblePages = 10;
    private final int postForPage =15;

    @GetMapping("/create")
    public String createPost(@RequestParam("category") Long categoryId, Model model
                            ,HttpSession httpSession){

        checkAuthority.checkLogin(httpSession);

        Category category = categoryService.findById(categoryId);
        MiddleCategory middleCategory = category.getMiddleCategory();

        model.addAttribute("category", category);
        model.addAttribute("middleCategory", middleCategory);
        return "createPostForm";
    }

    @PostMapping("/create")
    public String createPost(PostForm postForm, Model model,
                             HttpSession httpSession) {

        checkAuthority.checkOwner(httpSession, postForm.getWriter());

        postService.createPost(postForm);

        Long categoryId = Long.parseLong(postForm.getCategoryId());
        return "redirect:/post/board?category="+categoryId+"&page=1";
    }

    @GetMapping("/activity")
    public String activity(Model model, HttpSession httpSession,
                           @RequestParam(required = false) Integer postPage,
                           @RequestParam(required = false) Integer commentPage){

        if(postPage == null){ postPage =1; }
        if(commentPage == null){ commentPage =1; }
        String loginId = (String)httpSession.getAttribute("loginId");
        if(loginId == null){
            throw new RuntimeException("Wrong access, login session is null");
        }
        // side bar

        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        ActivityForm myPosts = postService.getActivityPost(loginId,postPage, postForPage);
        ActivityForm myComments = commentService.getActivityComment(loginId,commentPage, postForPage);

        // pagination
        model.addAttribute("visiblePages", visiblePages);
        model.addAttribute("totalPost", myPosts.getTotalCnt());
        model.addAttribute("totalComment", myComments.getTotalCnt());

        model.addAttribute("postPageNow", postPage);
        model.addAttribute("commentPageNow", commentPage);

        // print posts
        model.addAttribute("postForPage", postForPage);
        model.addAttribute("postList", myPosts.getResultList());
        model.addAttribute("commentList", myComments.getResultList());

        return "activityPage";
    }

    @GetMapping("/board")
    public String board(@RequestParam("category") Long categoryId,
                        @RequestParam(required = false) Integer page,
                        Model model) {

        if(page == null){ page =1; }

        // side bar

        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        // categories
        Category category = categoryService.findById(categoryId);

        MiddleCategory middleCategory = category.getMiddleCategory();
        model.addAttribute("middleCategory", middleCategory);


        // pagination

        model.addAttribute("visiblePages", visiblePages);
        model.addAttribute("totalPost", category.getPostCnt());
        model.addAttribute("pageNow", page);


        // print posts

        model.addAttribute("postForPage", postForPage);
        model.addAttribute("category", category);
        List<Post> postList = categoryService.getPostsInCategory(category, page, postForPage);

        model.addAttribute("postList", postList);

        return "board";
    }

    @GetMapping("/read")
    public String readPost(@RequestParam Long post, Model model){

        // side bar

        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        // post

        Post postToRead = postService.readPost(post);
        model.addAttribute("post", postToRead);

        return "postRead";
    }

    @GetMapping("/edit")
    public String editPostForm(@RequestParam("post") Long postId, Model model){

        Post post = postService.findById(postId);

        model.addAttribute("post", post);
        model.addAttribute("content", post.getContent().replace("<br>","\n"));
        model.addAttribute("category", post.getCategory());
        model.addAttribute("middleCategory", post.getCategory().getMiddleCategory());

        return "editPostForm";
    }

    @PostMapping("/edit")
    public String editPostForm(@RequestParam("post") Long postId, PostForm postForm,
                               HttpSession httpSession){

        Post post = postService.findById(postId);

        boolean isOwner = checkAuthority.checkOwner(httpSession, post.getWriter());
        boolean isAdmin = checkAuthority.checkAdmin(httpSession);
        if(!isAdmin && !isOwner){ throw new RuntimeException("You do not have access rights."); }

        postService.editPost(postId, postForm);
        return "redirect:/post/read?post="+postId;
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("post") Long postId,
                             HttpSession httpSession){

        Post post = postService.findById(postId);
        long categoryId = post.getCategory().getId();
        int page = 1;

        boolean isOwner = checkAuthority.checkOwner(httpSession, post.getWriter());
        boolean isAdmin = checkAuthority.checkAdmin(httpSession);
        if(!isAdmin && !isOwner){ throw new RuntimeException("You do not have access rights."); }

        postService.deletePost(postId);
        return "redirect:/post/board?category="+categoryId+"&page="+page;
    }
}
