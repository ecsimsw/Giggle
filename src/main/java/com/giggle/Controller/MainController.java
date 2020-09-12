package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.*;
import com.giggle.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final MainCategoryService mainCategoryService;
    private final PageService pageService;

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
    public String addImg(MultipartHttpServletRequest request) throws IOException {
        String resourceSrc = "\\static\\file\\mainBoardImg";
        String nameStr = "add_";
        int limitCnt=5;

        MultipartFile[] multipartFiles = new MultipartFile[limitCnt];

        for(int i =0; i<limitCnt; i++){
            MultipartFile requestFile = request.getFile(nameStr+i);
            multipartFiles[i] = requestFile;
        }

        pageService.addImgBoard(multipartFiles, resourceSrc );

        return "redirect:/main/edit/imgBoard";
    }

}
