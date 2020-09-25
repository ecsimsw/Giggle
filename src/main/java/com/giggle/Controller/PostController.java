package com.giggle.Controller;

import com.giggle.Domain.Entity.*;
import com.giggle.Domain.Form.PostForm;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MainCategoryService;
import com.giggle.Service.PostService;
import com.giggle.Validator.CheckAuthority;
import lombok.RequiredArgsConstructor;
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
    private final CategoryService categoryService;
    private final MainCategoryService mainCategoryService;

    @GetMapping("/create")
    public String createPost(@RequestParam("category") Long categoryId, Model model
                            ,HttpSession httpSession){

        if(httpSession.getAttribute("loginId")==null){
            throw new RuntimeException("Wrong access");
        }

        Category category = categoryService.findById(categoryId);
        MiddleCategory middleCategory = category.getMiddleCategory();

        model.addAttribute("category", category);
        model.addAttribute("middleCategory", middleCategory);
        return "createPostForm";
    }

    @PostMapping("/create")
    public String createPost(PostForm postForm, Model model,
                             HttpSession httpSession) {

        String loginId = (String)httpSession.getAttribute("loginId");
        if(loginId==null || postForm.getWriter().equals(loginId)){
            throw new RuntimeException("Wrong access");
        }

        postService.createPost(postForm);

        Long categoryId = Long.parseLong(postForm.getCategoryId());
        return "redirect:/post/board?category="+categoryId+"&page=1";
    }

    @GetMapping("/board")
    public String board(@RequestParam("category") Long categoryId,
                        @RequestParam(required = false) Integer page,
                        Model model) {

        if(page == null){ page =1; }

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

        String authority = (String) httpSession.getAttribute("authority");
        boolean isAdmin = authority.equals("admin") || authority.equals("master");
        boolean isPostOwner = httpSession.getAttribute("loginId").equals(post.getWriter());
        if(!isAdmin && !isPostOwner){ throw new RuntimeException("Wrong access"); }

//        CheckAuthority.checkAuthority(httpSession, post.getWriter());

        postService.editPost(postId, postForm);
        return "redirect:/post/read?post="+postId;
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("post") Long postId,
                             HttpSession httpSession){

        Post post = postService.findById(postId);

        String authority = (String) httpSession.getAttribute("authority");
        boolean isAdmin = authority.equals("admin") || authority.equals("master");
        boolean isPostOwner = httpSession.getAttribute("loginId").equals(post.getWriter());
        if(!isAdmin && !isPostOwner){ throw new RuntimeException("Wrong access"); }

        long categoryId = post.getCategory().getId();
        int page = 1;

        postService.deletePost(postId);
        return "redirect:/post/board?category="+categoryId+"&page="+page;
    }
}
