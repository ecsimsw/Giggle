package com.giggle.Controller;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.CommunityType;
import com.giggle.Domain.Entity.Post;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MemberService;
import com.giggle.Service.PostService;
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

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session){
        String loginId = (String)session.getAttribute("loginId");

        List<String> communityNameList = new ArrayList<>();
        List<List<String>> communityList = new ArrayList<>();

        for(CommunityType communityType : CommunityType.values()){
            communityNameList.add(communityType.name());
            List<String> categoryNameList = categoryService.getCategoryNamesInCommunity(communityType);
            communityList.add(categoryNameList);
        }

        model.addAttribute("communityNameList", communityNameList);
        model.addAttribute("communityList", communityList);
        model.addAttribute("loginId",loginId);

        return "mainPage";
    }



}
