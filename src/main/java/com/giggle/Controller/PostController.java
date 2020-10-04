package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.ActivityForm;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Service.*;
import com.giggle.Validator.CheckAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final LikeService likeService;

    @Autowired CheckAuthority checkAuthority;
    @Autowired ObjectMapper objectMapper;

    private final int visiblePages = 10;
    private final int postForPage =15;
    private final int hotPostCnt = 7;

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
    public String createPost(PostForm postForm,
                             HttpSession httpSession) {

        String loginId = checkAuthority.checkLogin(httpSession);

        Member member = memberService.getByLoginId(loginId);

        postService.createPost(postForm, member);

        Long categoryId = Long.parseLong(postForm.getCategoryId());
        return "redirect:/post/board?category="+categoryId+"&page=1";
    }

    @GetMapping("/activity")
    public String activity(Model model, HttpSession httpSession,
                           @RequestParam(required = false) Integer postPage,
                           @RequestParam(required = false) Integer commentPage){

        // top bar
        String loginId = checkAuthority.checkLogin(httpSession);
        model.addAttribute("loginId",loginId);

        Member member = memberService.getByLoginId(loginId);
        model.addAttribute("profileImg", member.getProfileImg());

        if(postPage == null){ postPage =1; }
        if(commentPage == null){ commentPage =1; }

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
                        Model model, HttpSession session) {

        // top bar
        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);

        if(loginId != null){
            Member member = memberService.getByLoginId(loginId);
            model.addAttribute("profileImg", member.getProfileImg());
        }
        else{
            model.addAttribute("profileImg", "stranger.png");
        }

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
    public String readPost(@RequestParam Long post, Model model, HttpSession session){

        // top bar

        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);

        if(loginId != null){
            Member member = memberService.getByLoginId(loginId);
            model.addAttribute("profileImg", member.getProfileImg());
            model.addAttribute("authority", member.getMemberType());
        }
        else{
            model.addAttribute("profileImg", "stranger.png");
        }

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
        boolean isMaster = checkAuthority.checkMaster(httpSession);
        if(!isMaster && !isOwner){ throw new RuntimeException("You do not have access rights."); }

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
        boolean isMaster = checkAuthority.checkMaster(httpSession);
        if(!isMaster && !isOwner){ throw new RuntimeException("You do not have access rights."); }

        postService.deletePost(postId);
        return "redirect:/post/board?category="+categoryId+"&page="+page;
    }


    @PostMapping("/like")
    @ResponseBody
    public String likePost(long postId, HttpSession httpSession) throws JsonProcessingException {
        String result;

        String loginId = (String) httpSession.getAttribute("loginId");

        if(loginId == null){
            result = "no login";
        }
        else{
            Member member = memberService.getByLoginId(loginId);
            Post post = postService.findById(postId);

            int r = likeService.likePost(post, member, hotPostCnt);
            if(r == 1){
                result = "like";
            }
            else{
                result = "dislike";
            }
        }

        return objectMapper.writeValueAsString(result);
    }
}
