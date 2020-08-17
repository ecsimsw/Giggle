package com.giggle.Controller;

import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category/create")
    public String createCategory(){
        return "createCategoryForm";
    }

    @PostMapping("/category/create")
    public String createCategory(CreateCategoryForm createCategoryForm, Model model){
        List<String> categoryNames = categoryService.getCategoryNames();
        if(categoryNames.contains(createCategoryForm.getName())){
            model.addAttribute("message","category already existent");
            return "redirect:/category/create";
        }
        else{
            categoryService.save(createCategoryForm);
        }
        return "redirect:/post/"+createCategoryForm.getName();
    }
}
