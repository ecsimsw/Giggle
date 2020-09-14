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

    private final int newPostCntToPrint = 7;
    private final String nameId = "add_";  // edit/imgBoard 에서 사진 name 형식 : add_1 ~ add_n
    private final int limitAdditionImgCnt = 5;  // edit/imgBoard 에서 추가할 수 있는 사진 개수 제한

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
        int newPostCntToPrint = 7;    // mainPage에 포스트할 새로운 글의 개수를 지정한다.

        int totalPostCnt = 0;   // 전체 글의 개수 표시를 위함

        for(MainCategory m : mainCategoryList) { totalPostCnt += m.getPostCnt(); }

        List<Post> newPostList = postService.getNewPost(totalPostCnt, newPostCntToPrint);

        model.addAttribute("totalPostCnt", totalPostCnt);
        model.addAttribute("newPostList", newPostList);


        // main image board
        List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc("/static/mainBoardImg");
        model.addAttribute("mainBoardImgSrc",mainBoardImgSrc);
        return "mainPage";
    }


    @GetMapping("/edit/imgBoard")
    public String editImgBoard(Model model){

        // main image board
        List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc("/static/mainBoardImg");
        model.addAttribute("mainBoardImgSrc",mainBoardImgSrc);

        // limitAdditionImgCnt
        model.addAttribute("limitAdditionImgCnt", limitAdditionImgCnt);

        List<MainBoardImg> allMainBoardImg = pageService.getAllMainBoardImg();
        model.addAttribute("mainBoardImg",allMainBoardImg);

        return "editImgBoard";
    }

    @GetMapping("/edit/imgBoard/delete")
    public String editImgBoard(@RequestParam(value="imageFiles") long[] idArr, HttpServletRequest request){

        String resourceSrc = request.getServletContext().getRealPath("/mainBoardImg");

        pageService.deleteImgArr(idArr, resourceSrc);

        return "redirect:/main/edit/imgBoard";
    }

    @PostMapping("/edit/imgBoard/add")
    public String addImg(MultipartHttpServletRequest multipartHttpServletRequest,
                         HttpServletRequest request) throws IOException {

        String resourceSrc = request.getServletContext().getRealPath("/mainBoardImg");

        MultipartFile[] multipartFiles = new MultipartFile[limitAdditionImgCnt];

        for(int i =0; i<limitAdditionImgCnt; i++){
            MultipartFile requestFile = multipartHttpServletRequest.getFile(nameId+i);
            multipartFiles[i] = requestFile;
        }

        pageService.addImgBoard(multipartFiles, resourceSrc);

        return "redirect:/main/edit/imgBoard";
    }

}
