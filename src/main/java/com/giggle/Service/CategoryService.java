package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.CommunityType;
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
    public void createCategory(CommunityType communityType, CreateCategoryForm createCategoryForm){
        Category newCategory = new Category();
        newCategory.setName(createCategoryForm.getName());
        newCategory.setCommunityType(communityType);
        newCategory.setDescription(createCategoryForm.getDescription());
        newCategory.setPostCnt(0);
        categoryRepository.save(newCategory);
    }

    public List<String> getCategoryNamesInCommunity(CommunityType communityType){
        List<String> categoryNames = new ArrayList<>();
        List<Category> categoriesInCommunity = categoryRepository.findAllCategoryInCommunity(communityType);

        for(Category c : categoriesInCommunity){
            categoryNames.add(c.getName());
        }
        return categoryNames;
    }
}
