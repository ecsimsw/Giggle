package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(CreateCategoryForm createCategoryForm){
        Category newCategory = new Category();
        newCategory.setName(createCategoryForm.getName());
        newCategory.setDescription(createCategoryForm.getDescription());
        newCategory.setPostCnt(0);

        categoryRepository.save(newCategory);
    }

    public List<String> getCategoryNames(){
        List<String> categoryNames = new ArrayList<>();
        List<Category> allCategory = categoryRepository.findAllCategory();

        for(Category c : allCategory){
            categoryNames.add(c.getName());
        }

        return categoryNames;
    }
}
