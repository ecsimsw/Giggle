package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Entity.CreatePostForm;
import com.giggle.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;


    @GetMapping("/post/create")
    public String create(){
        return "createPostForm";
    }

    @PostMapping("/post/create")
    @ResponseBody
    public String create(CreatePostForm createPostForm) throws JsonProcessingException {

        String result = objectMapper.writeValueAsString(createPostForm);
        return result;
    }
}
