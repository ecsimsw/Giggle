package com.giggle.Controller;

import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.MemberType;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Domain.Form.CreateMiddleCategoryForm;
import com.giggle.Service.CategoryService;
import com.giggle.Validation.CategoryValidator;
import com.giggle.Validation.CheckAuthority;
import com.giggle.Validation.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired CategoryValidator categoryValidator;
    @Autowired CheckAuthority checkAuthority;

    @Permission(authority = MemberType.admin)
    @GetMapping("/create/mainCategory")
    public String createMainCategory(){
        return "createMainCategory";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/create/mainCategory")
    public String createMainCategory(CreateMainCategoryForm createMainCategoryForm, HttpSession httpSession){
        categoryService.createMainCategory(createMainCategoryForm);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/create/middleCategory")
    public String createMiddleCategory(@RequestParam Long mainCategory, Model model){
        model.addAttribute("mainCategoryId",mainCategory);
        return "createMiddleCategory";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/create/middleCategory")
    public String createMiddleCategory(CreateMiddleCategoryForm createMiddleCategoryForm, HttpSession httpSession){
        categoryService.createMiddleCategory(createMiddleCategoryForm);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/create/category")
    public String createCategory(@RequestParam Long mainCategory, @RequestParam Long middleCategory, Model model){

        model.addAttribute("mainCategoryId", mainCategory);
        model.addAttribute("middleCategoryId", middleCategory);
        return "createCategory";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/create/category")
    public String createCategory(@Valid CreateCategoryForm createCategoryForm,
                                 BindingResult bindingResult,
                                 HttpSession httpSession){

        categoryValidator.validate(createCategoryForm, bindingResult);
        if(bindingResult.hasErrors()){ throw new RuntimeException("Invalid categoryForm"); }

        categoryService.createCategory(createCategoryForm);
        return "redirect:/main";
    }

    @Permission(authority = MemberType.admin)
    @GetMapping("/delete")
    public String deleteMainCategory(Model model){

        // sideBar
        List<MainCategory> mainCategoryList = categoryService.getAllMainCategory();
        model.addAttribute("mainCategoryList", mainCategoryList);

        return "deleteCategory";
    }

    @Permission(authority = MemberType.admin)
    @PostMapping("/delete")
    public String deleteMainCategory(@RequestParam String selectedCategory,
                                     HttpSession httpSession) {
        try {
            String[] typeId = selectedCategory.split("/");
            String type = typeId[0];
            long id = Long.parseLong(typeId[1]);

            if(type.equals("main")){ categoryService.deleteMainCategory(id); }
            else if(type.equals("mid")){ categoryService.deleteMiddleCategory(id); }
            else if(type.equals("cat")){ categoryService.deleteCategory(id); }
            else{
                throw new RuntimeException("Error in selectedCategory");
            }
        } catch (Exception e){
            throw new RuntimeException("Error in selectedCategory");
        }

        return "redirect:/main";
    }
}
