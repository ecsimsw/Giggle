package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreatePostForm;
import com.giggle.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    @GetMapping("/post/create/{category}")
    public String create(@PathVariable String category, Model model){
        model.addAttribute("category", category);
        return "createPostForm";
    }

    @PostMapping("/post/create")
    public String create(CreatePostForm createPostForm) throws JsonProcessingException {
        String result = objectMapper.writeValueAsString(createPostForm);
        Post newPost = new Post();
        newPost.setCategory(createPostForm.getCategory());
        newPost.setTitle(createPostForm.getTitle());
        newPost.setWriter("tester");
        newPost.setContent(createPostForm.getContent());

        postService.createPost(newPost);
        return "redirect:/post/"+createPostForm.getCategory();
    }
}
