package com.giggle.Controller;

import com.giggle.Domain.Entity.CommunityType;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Service.CategoryService;
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
