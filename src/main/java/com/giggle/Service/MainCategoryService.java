package com.giggle.Service;

import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Repository.MainCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Transactional
    public void createMainCategory(CreateMainCategoryForm createMainCategoryForm){
        MainCategory newMainCategory = new MainCategory();

        newMainCategory.setName(createMainCategoryForm.getName());
        newMainCategory.setDescription(createMainCategoryForm.getDescription());

        mainCategoryRepository.save(newMainCategory);
    }

    public List<MainCategory> getAllMainCategory(){
        return mainCategoryRepository.findAllMainCategory();
    }
}
