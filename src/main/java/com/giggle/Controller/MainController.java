package com.giggle.Controller;

import com.giggle.Domain.Entity.*;
import com.giggle.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final MainCategoryService mainCategoryService;
    private final MiddleCategoryService middleCategoryService;

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session){
        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);


        // sideBar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();

        model.addAttribute("mainCategoryList", mainCategoryList);

        //

//        List<String> communityNameList = new ArrayList<>();
//        List<List<String>> communityList = new ArrayList<>();
//
//        int totalPostCntInCommunity =0;
//
//        for(CommunityType communityType : CommunityType.values()){
//            communityNameList.add(communityType.name());
//            List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(communityType);
//            for(String categoryName : categoryNameList){
//                totalPostCntInCommunity += categoryService.getTotalCnt(communityType.name(), categoryName);
//            }
//            communityList.add(categoryNameList);
//        }
//
//        model.addAttribute("communityNameList", communityNameList);
//        model.addAttribute("communityList", communityList);
//
//        // new Posts
//
//        int newPostCntToPrint = 7;
//
//        List<Post> newPostList = postService.getNewPost(totalPostCntInCommunity, newPostCntToPrint);
//        model.addAttribute("newPostList", newPostList);

        return "mainPage";
    }



}
