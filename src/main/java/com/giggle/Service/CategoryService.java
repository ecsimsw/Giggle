package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
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
    public void save(String categoryName){
        Category newCategory = new Category();
        newCategory.setName(categoryName);
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
