package com.giggle.Controller;

import com.giggle.Domain.Entity.*;
import com.giggle.Service.*;
import com.giggle.Validation.CheckAuthority;
import com.giggle.Validation.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final PageService pageService;
    private final MemberService memberService;

    private final int newPostCntToPrint = 7; // mainPage에 포스트할 newPost 글의 개수를 지정한다.
    private final int limitAdditionImgCnt = 5;  // edit/imgBoard 에서 한번에 추가할 수 있는 사진 개수 제한
    private final String nameId = "add_";  // edit/imgBoard 에서 사진 name 형식 : add_1 ~ add_n

    @Value("${s3.path}") private String s3Path;

    @GetMapping("")
    public String mainPage(Model model, HttpSession session){

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

        // sideBar
        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);


        // newPosts, hotPosts

        int totalPostCnt = 0;   // 전체 글의 개수 표시를 위함
        for(MainCategory m : mainCategoryList) { totalPostCnt += m.getPostCnt(); }
        model.addAttribute("totalPostCnt", totalPostCnt);

        List<Post> newPostList = postService.getNewPost(totalPostCnt, newPostCntToPrint);
        model.addAttribute("newPostList", newPostList);

        List<HotPost> hotPostList = postService.getHotPostList();
        model.addAttribute("hotPostList", hotPostList);

        // main image board

//        List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc("/static/mainBoardImg");
        List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc(s3Path+"/mainBoardImg");
        model.addAttribute("mainBoardImgSrc",mainBoardImgSrc);

        // shortCut

        List<ShortCut> shortCutList = pageService.getAllShortCut();
        model.addAttribute("shortCutList", shortCutList);

        // dashBoard

        List<DashBoard> dashBoardList = pageService.getAllDashBoard();

        for(DashBoard dashBoard : dashBoardList){

            if(dashBoard.getType() == DashBoardType.latestPost){
                Category category = categoryService.findById(dashBoard.getLinkId());
                if(category!=null){
                    List<Post> posts = category.getPosts();

                    if(posts.size()==0){
                        dashBoard.setContent("no post");
                    }
                    else{
                        dashBoard.setContent(posts.get(posts.size()-1).getContent());
                    }
                }
            }

            else if(dashBoard.getType() == DashBoardType.linkPost){
                Post post = postService.findById(dashBoard.getLinkId());
                if(post != null){
                    dashBoard.setContent(post.getContent());
                }
            }
        }

        model.addAttribute("dashBoardList", dashBoardList);

        return "mainPage";
    }

    // edit shortCut

    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/shortCut")
    public String editShortCut(Model model){
        List<ShortCut> shortCutList = pageService.getAllShortCut();
        model.addAttribute("shortCutList", shortCutList);

        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        return "editShortCut";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/shortCut/add")
    public String editShortCutAdd(@RequestParam("selectedCategory") long categoryId,
                                  @RequestParam String description,
                                  @RequestParam String color){

        Category category = categoryService.findById(categoryId);
        pageService.addShortCut(category, description, color);
        return "redirect:/main/edit/shortCut";
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/shortCut/delete")
    public String editShortCutDelete(@RequestParam(value="shortCut") long[] idArr)
    {
        pageService.deleteShortCutArr(idArr);
        return "redirect:/main/edit/shortCut";
    }


    // ---- edit dashBoard
    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/dashBoard/add")
    public String editDashBoard(){
        return "addDashBoard";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/add")
    public String editDashBoardAdd(@RequestParam DashBoardType type,
                                   @RequestParam String width,
                                   @RequestParam String height){
        long dashBoardId = pageService.addDashBoard(type, width, height);

        return "redirect:/main/edit/dashBoard?id="+dashBoardId;
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/dashBoard")
    public String editDashBoard(@RequestParam long id, Model model) {
        DashBoard dashBoard = pageService.findDashBoardById(id);
        model.addAttribute("dashBoard", dashBoard);

        String content = dashBoard.getContent();
        content = content != null ? content.replace("<br>","\n") : null;
        model.addAttribute("dashBoard_content", content);

        if(dashBoard.getType()== DashBoardType.latestPost){
            List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
            model.addAttribute("mainCategoryList", mainCategoryList);
        }

        else if(dashBoard.getType() == DashBoardType.imgBoard){
            // main image board
//            List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc("/static/mainBoardImg");
            List<String> mainBoardImgSrc = pageService.getMainBoardImgSrc(s3Path+"/mainBoardImg");
            model.addAttribute("mainBoardImgSrc",mainBoardImgSrc);

            // limitAdditionImgCnt
            model.addAttribute("limitAdditionImgCnt", limitAdditionImgCnt);

            List<MainBoardImg> allMainBoardImg = pageService.getAllMainBoardImg();
            model.addAttribute("mainBoardImg",allMainBoardImg);
        }

        return "editDashBoard";
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/dashBoard/delete")
    public String editDashBoardDelete(@RequestParam long id) {
        pageService.deleteDashBoard(id);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/type")
    public String editDashBoardType(@RequestParam long id,
                                    @RequestParam DashBoardType type,
                                    @RequestParam String width,
                                    @RequestParam String height){

        pageService.editDashBoardType(id, type, width, height);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/freePost")
    public String editDashBoardFreePost(@RequestParam long id,
                                        @RequestParam String title,
                                        @RequestParam String content){
        pageService.editDashBoardFreePost(id, title, content);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/linkPost")
    public String editDashBoardLinkPost(@RequestParam long id,
                                        @RequestParam String title,
                                        @RequestParam long linkId){

        pageService.editDashBoardLinkPost(id, title, linkId);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/latestPost")
    public String editDashBoardLatestPost(@RequestParam long id,
                                          @RequestParam String title,
                                          @RequestParam long linkId){

        pageService.editDashBoardLatestPost(id, title, linkId);
        return "redirect:/main";
    }

    // edit img board with window
//    @Permission(authority = MemberType.admin)
//    @GetMapping("/edit/dashBoard/imgBoard/deleteImg")
//    public String editImgBoard(@RequestParam(value="imageFiles") long[] idArr, HttpServletRequest request){
//
//        String resourceSrc = request.getServletContext().getRealPath("/mainBoardImg");
//
//        pageService.deleteImgArr(idArr, resourceSrc);
//
//        return "redirect:/main";
//    }

    // edit img board with s3
    @Permission(authority = MemberType.admin)
    @GetMapping("/edit/dashBoard/imgBoard/deleteImg")
    public String editImgBoard(@RequestParam(value="imageFiles") long[] idArr, HttpServletRequest request){

        String basePath = "mainBoardImg";
        pageService.deleteImgArrWithS3(idArr, basePath);

        return "redirect:/main";
    }

    // addImg with window
//    @Permission(authority = MemberType.admin)
//    @PostMapping("/edit/dashBoard/imgBoard/addImg")
//    public String addImg(MultipartHttpServletRequest multipartHttpServletRequest,
//                         HttpServletRequest request) throws IOException {
//
//        String resourceSrc = request.getServletContext().getRealPath("/mainBoardImg");
//
//        MultipartFile[] multipartFiles = new MultipartFile[limitAdditionImgCnt];
//
//        for(int i =0; i<limitAdditionImgCnt; i++){
//            MultipartFile requestFile = multipartHttpServletRequest.getFile(nameId+i);
//            multipartFiles[i] = requestFile;
//        }
//
//        pageService.addImgBoard(multipartFiles, resourceSrc);
//
//        return "redirect:/main";
//    }

    // addImg with S3
    @Permission(authority = MemberType.admin)
    @PostMapping("/edit/dashBoard/imgBoard/addImg")
    public String addImg(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException {

        String basePath = "mainBoardImg";

        MultipartFile[] multipartFiles = new MultipartFile[limitAdditionImgCnt];

        for(int i =0; i<limitAdditionImgCnt; i++){
            MultipartFile requestFile = multipartHttpServletRequest.getFile(nameId+i);
            multipartFiles[i] = requestFile;
        }

        pageService.addImgBoardWithS3(multipartFiles, basePath);

        return "redirect:/main";
    }
}
