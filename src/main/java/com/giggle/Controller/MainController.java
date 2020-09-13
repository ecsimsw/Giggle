package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.*;
import com.giggle.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final MainCategoryService mainCategoryService;
    private final PageService pageService;


    @Autowired
    ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper;

    @GetMapping("")
    public String mainPage(Model model, HttpSession session){
        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);

        // sideBar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();

        model.addAttribute("mainCategoryList", mainCategoryList);

        // new Posts
        int newPostCntToPrint = 7;

        int totalPostCnt = 0;

        for(MainCategory m : mainCategoryList) {
            totalPostCnt += m.getPostCnt();
        }

        List<Post> newPostList = postService.getNewPost(totalPostCnt, newPostCntToPrint);

        model.addAttribute("totalPostCnt", totalPostCnt);
        model.addAttribute("newPostList", newPostList);

        return "mainPage";
    }


    @GetMapping("/edit/imgBoard")
    public String editImgBoard(){
        return "editImgBoard";
    }

    @PostMapping("/edit/imgBoard/add")
    @ResponseBody
    public String addImg(MultipartHttpServletRequest multipartHttpServletRequest,
                         HttpServletRequest request) throws IOException {

        URL r = this.getClass().getResource("/");
        // request.getServletContext.getRealPath() 적용 안됨
        Resource resource = resourceLoader.getResource("classpath:file/mainBoardImg/image1.png"); // 파일 지정 필요

        resource.exists();
        resource.getFile();
        resource.getURI();

//        String resourceSrc = r.getPath();
//        resourceSrc+="resources/static/file/mainBoardImg";

        String resourceSrc = request.getServletContext().getRealPath("/mainBoardImg");


        String nameStr = "add_";
        int limitCnt=5;

        MultipartFile[] multipartFiles = new MultipartFile[limitCnt];

        for(int i =0; i<limitCnt; i++){
            MultipartFile requestFile = multipartHttpServletRequest.getFile(nameStr+i);
            multipartFiles[i] = requestFile;
        }

        pageService.addImgBoard(multipartFiles, resourceSrc);


        return r.getPath();
    }

}
