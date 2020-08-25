package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

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

    @GetMapping("/create/category")
    public String createCategory(@RequestParam Long mainCategory, @RequestParam Long middleCategory, Model model){
        model.addAttribute("mainCategoryId", mainCategory);
        model.addAttribute("middleCategoryId", middleCategory);

        return "createCategory";
    }

    @PostMapping("/create/category")
    @ResponseBody
    public String createCategory(CreateCategoryForm createCategoryForm) throws JsonProcessingException {

        categoryService.createCategory(createCategoryForm);
        return "redirect:/main";
    }

}
