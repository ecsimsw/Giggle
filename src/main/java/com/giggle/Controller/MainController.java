package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.*;
import com.giggle.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final CategoryService categoryService;
    private final PostService postService;
    private final MainCategoryService mainCategoryService;
    private final PageService pageService;

    private final int newPostCntToPrint = 7; // mainPage에 포스트할 새로운 글의 개수를 지정한다.
    private final String nameId = "add_";  // edit/imgBoard 에서 사진 name 형식 : add_1 ~ add_n
    private final int limitAdditionImgCnt = 5;  // edit/imgBoard 에서 추가할 수 있는 사진 개수 제한

    private final ObjectMapper objectMapper;


    @GetMapping("")
    public String mainPage(Model model, HttpSession session) throws JsonProcessingException {
        String loginId = (String)session.getAttribute("loginId");
        model.addAttribute("loginId",loginId);

        // sideBar

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();

        model.addAttribute("mainCategoryList", mainCategoryList);

        int totalPostCnt = 0;   // 전체 글의 개수 표시를 위함
        for(MainCategory m : mainCategoryList) { totalPostCnt += m.getPostCnt(); }

        List<Post> newPostList = postService.getNewPost(totalPostCnt, newPostCntToPrint);

        model.addAttribute("totalPostCnt", totalPostCnt);
        model.addAttribute("newPostList", newPostList);


        // main image board
        List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc("/static/mainBoardImg");
        model.addAttribute("mainBoardImgSrc",mainBoardImgSrc);


        // shortCut
        List<ShortCut> shortCutList = pageService.getAllShortCut();
        model.addAttribute("shortCutList", shortCutList);


        // dashBoard
        List<DashBoard> dashBoardList = pageService.getAllDashBoard();
        model.addAttribute("dashBoardList", dashBoardList);

        return "mainPage";
    }


    // edit img board

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


    // edit shortCut

    @GetMapping("/edit/shortCut")
    public String editShortCut(Model model){
        List<ShortCut> shortCutList = pageService.getAllShortCut();
        model.addAttribute("shortCutList", shortCutList);

        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        return "editshortCut";
    }


    @PostMapping("/edit/shortCut/add")
    public String editShortCutAdd(@RequestParam("selectedCategory") long categoryId,
                                  @RequestParam String description,
                                  @RequestParam String color){
        Category category = categoryService.findById(categoryId);
        pageService.addShortCut(category, description, color);
        return "redirect:/main/edit/shortCut";
    }


    @GetMapping("/edit/shortCut/delete")
    public String editShortCutDelete(@RequestParam(value="shortCut") long[] idArr)
    {
        pageService.deleteShortCutArr(idArr);
        return "redirect:/main/edit/shortCut";
    }


    // ---- edit dashBoard

    @GetMapping("/add/dashBoard")
    public String editDashBoard(){
        return "addDashBoard";
    }

    @PostMapping("/add/dashBoard")
    public String editDashBoardAdd(@RequestParam DashBoardType type,
                                   @RequestParam String width,
                                   @RequestParam String height) {
        pageService.addDashBoard(type, width, height);
        return "redirect:/main/add/dashBoard";
    }

    @GetMapping("/edit/dashBoard")
    public String editDashBoard(@RequestParam long id, Model model) {
        DashBoard dashBoard = pageService.findDashBoardById(id);
        model.addAttribute("dashBoard", dashBoard);
        model.addAttribute("dashBoard_content",  dashBoard.getContent().replace("<br>","\n"));

        if(dashBoard.getType()== DashBoardType.latestPost){
            List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
            model.addAttribute("mainCategoryList", mainCategoryList);
        }

        return "editDashBoard";
    }

    @GetMapping("/edit/dashBoard/delete")
    public String editDashBoardDelete(@RequestParam long id) {
        pageService.deleteDashBoard(id);
        return "redirect:/main";
    }

    @PostMapping("/edit/dashBoard/type")
    public String editDashBoardType(@RequestParam long id,
                                    @RequestParam DashBoardType type,
                                    @RequestParam String width,
                                    @RequestParam String height){
        pageService.editDashBoardType(id, type, width, height);
        return "redirect:/main/edit/dashBoard?id="+id;
    }

    @PostMapping("/edit/dashBoard/freePost")
    public String editDashBoardFreePost(@RequestParam long id,
                                        @RequestParam String title,
                                        @RequestParam String content) {
        pageService.editDashBoardFreePost(id, title, content);
        return "redirect:/main";
    }

    @PostMapping("/edit/dashBoard/linkPost")
    public String editDashBoardLinkPost(@RequestParam long id,
                                        @RequestParam String title,
                                        @RequestParam long linkId) {
        pageService.editDashBoardLinkPost(id, title, linkId);
        return "redirect:/main";
    }

    @PostMapping("/edit/dashBoard/latestPost")
    public String editDashBoardLatestPost(@RequestParam long id,
                                          @RequestParam String title,
                                          @RequestParam long linkId) {
        pageService.editDashBoardLatestPost(id, title, linkId);
        return "redirect:/main";
    }
}
