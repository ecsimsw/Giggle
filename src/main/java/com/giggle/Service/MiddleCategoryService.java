package com.giggle.Service;

import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.MiddleCategory;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Domain.Form.CreateMiddleCategoryForm;
import com.giggle.Repository.MainCategoryRepository;
import com.giggle.Repository.MiddleCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MiddleCategoryService {

    private final MiddleCategoryRepository middleCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;

    @Transactional
    public void createMiddleCategory(CreateMiddleCategoryForm createMiddleCategoryForm){

        MiddleCategory middleCategory = new MiddleCategory();
        middleCategory.setMainCategory(mainCategoryRepository.findAllMainCategory().get(0));
        middleCategory.setName(createMiddleCategoryForm.getName());
        middleCategory.setPostCnt(0);

        middleCategoryRepository.save(middleCategory);
    }

}
