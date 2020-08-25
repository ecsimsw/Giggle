package com.giggle.Controller;

import com.giggle.Domain.Entity.CommunityType;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Domain.Form.CreateMiddleCategoryForm;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MainCategoryService;
import com.giggle.Service.MiddleCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final MainCategoryService mainCategoryService;
    private final MiddleCategoryService middleCategoryService;

    @GetMapping("/create/mainCategory")
    public String createMainCategory(){
        return "createMainCategory";
    }

    @PostMapping("/create/mainCategory")
    public String createMainCategory(CreateMainCategoryForm createMainCategoryForm){
        mainCategoryService.createMainCategory(createMainCategoryForm);
        return "redirect:/main";
    }

    @GetMapping("/create/middleCategory")
    public String createMiddleCategory(@RequestParam Long mainCategory, Model model){
        model.addAttribute("mainCategoryId",mainCategory);
        return "createMiddleCategory";
    }

    @PostMapping("/create/middleCategory")
    public String createMiddleCategory(CreateMiddleCategoryForm createMiddleCategoryForm){
        middleCategoryService.createMiddleCategory(createMiddleCategoryForm);
        return "redirect:/main";
    }

    @GetMapping("/create/{community}")
    public String createCategory(@PathVariable String community, Model model){
        model.addAttribute("communityType", community);
        return "createCategoryForm";
    }

    @PostMapping("/create/{community}")
    public String createCategory(@PathVariable String community, CreateCategoryForm createCategoryForm, Model model){
        CommunityType communityType = CommunityType.valueOf(community);

        List<String> categoryNames = categoryService.getCategoryNamesInCommunity(communityType);
        if(categoryNames.contains(createCategoryForm.getName())){
            model.addAttribute("message","category in that community is already existent");
            return "redirect:/category/create/"+community;
        }
        else{
            categoryService.createCategory(communityType, createCategoryForm);
        }
        return "redirect:/post/board/"+community+"/"+createCategoryForm.getName()+"?page=1";
    }
}
