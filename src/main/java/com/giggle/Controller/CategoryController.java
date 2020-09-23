package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Domain.Form.CreateMiddleCategoryForm;
import com.giggle.Service.CategoryService;
import com.giggle.Service.MainCategoryService;
import com.giggle.Service.MiddleCategoryService;
import com.giggle.Validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final MainCategoryService mainCategoryService;
    private final MiddleCategoryService middleCategoryService;

    @Autowired CategoryValidator categoryValidator;

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
    public String createCategory(@Valid CreateCategoryForm createCategoryForm
            , BindingResult bindingResult) throws JsonProcessingException {

        categoryValidator.validate(createCategoryForm, bindingResult);

        if(bindingResult.hasErrors()){
            throw new RuntimeException("Invalid categoryForm");
        }

        categoryService.createCategory(createCategoryForm);
        return "redirect:/main";
    }

    @GetMapping("/delete")
    public String deleteMainCategory(Model model){

        // sideBar
        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        return "deleteCategory";
    }

    @PostMapping("/delete")
    public String deleteMainCategory(@RequestParam String selectedCategory) throws Exception {
        String type;
        long id;
        try {
            String[] typeId = selectedCategory.split("/");
            type = typeId[0];
            id = Long.parseLong(typeId[1]);
        } catch (Exception e){
            throw new RuntimeException("Error in selectedCategory");
        }
        if(type.equals("main")){
            mainCategoryService.deleteMainCategory(id);
        }
        else if(type.equals("mid")){
           middleCategoryService.deleteMiddleCategory(id);
        }
        else if(type.equals("cat")){
           categoryService.deleteCategory(id);
        }
        else{
           throw new RuntimeException("Error in selectedCategory");
        }

        return "redirect:/main";
    }

    @GetMapping("/refreshPostCnt")
    public String refreshPostCnt(){
        categoryService.updateWholeCategoryPostCnt();
        return "redirect:/main";
    }
}
