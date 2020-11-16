package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.MiddleCategory;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Domain.Form.CreateMainCategoryForm;
import com.giggle.Domain.Form.CreateMiddleCategoryForm;
import com.giggle.Repository.CategoryRepository;
import com.giggle.Repository.MainCategoryRepository;
import com.giggle.Repository.MiddleCategoryRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MiddleCategoryRepository middleCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;

    /////// mainCategory Service

    @Transactional
    public void createMainCategory(CreateMainCategoryForm createMainCategoryForm){
        MainCategory mainCategory = new MainCategory();

        mainCategory.setName(createMainCategoryForm.getName());
        mainCategory.setDescription(createMainCategoryForm.getDescription());
        mainCategory.setPostCnt(0);
        mainCategoryRepository.save(mainCategory);
    }

    public List<MainCategory> getAllMainCategory(){
        return mainCategoryRepository.findAllMainCategory();
    }

    @Transactional
    public void deleteMainCategory(long id){
        mainCategoryRepository.deleteById(id);
    }


    ///////  middleCategory Service

    @Transactional
    public void createMiddleCategory(CreateMiddleCategoryForm createMiddleCategoryForm){
        MiddleCategory middleCategory = new MiddleCategory();
        middleCategory.setMainCategory(mainCategoryRepository.findById(createMiddleCategoryForm.getMainCategoryId()));
        middleCategory.setName(createMiddleCategoryForm.getName());
        middleCategory.setPostCnt(0);

        middleCategoryRepository.save(middleCategory);
    }

    @Transactional
    public void deleteMiddleCategory(long id){
        MiddleCategory middleCategory = middleCategoryRepository.findById(id);
        MainCategory mainCategory = middleCategory.getMainCategory();
        mainCategory.getMiddleCategoryList().remove(middleCategory);
        mainCategoryRepository.updatePostCnt(mainCategory.getId(), mainCategory.getPostCnt()-middleCategory.getPostCnt());
        middleCategoryRepository.deleteById(id);
    }

    /////// category Service

    @Transactional
    public void createCategory(CreateCategoryForm createCategoryForm){
        Category newCategory = new Category();

        newCategory.setName(createCategoryForm.getName());
        newCategory.setMainCatId(createCategoryForm.getMainCategoryId());
        newCategory.setMiddleCatId(createCategoryForm.getMiddleCategoryId());
        newCategory.setMiddleCategory(middleCategoryRepository.findById(createCategoryForm.getMiddleCategoryId()));
        newCategory.setDescription(createCategoryForm.getDescription());
        newCategory.setPostCnt(0);

        categoryRepository.save(newCategory);
    }

    public Category findById(Long id){
        return categoryRepository.findById(id);
    }

    public void updatePostCnt(long categoryId, int postCnt){
        Category category = categoryRepository.findById(categoryId);
        int dif = postCnt - category.getPostCnt();
        category.setPostCnt(postCnt);

        log.info("Category : "+ category.getId());
        log.info("middleCategory : "+ category.getMiddleCategory().getId());
        log.info("middleCategory postCnt : "+ category.getMiddleCategory().getPostCnt());

        MiddleCategory middleCategory = category.getMiddleCategory();
        middleCategoryRepository.updatePostCnt(middleCategory.getId(), middleCategory.getPostCnt()+dif);

        MainCategory mainCategory = middleCategory.getMainCategory();
        mainCategoryRepository.updatePostCnt(mainCategory.getId(), mainCategory.getPostCnt()+dif);
    }

    public void updateWholeCategoryPostCnt(){
        List<MainCategory> allMainCategory = mainCategoryRepository.findAllMainCategory();

        for(MainCategory mainCategory : allMainCategory){
            int mainPostCnt =0;

            for(MiddleCategory middleCategory : mainCategory.getMiddleCategoryList()){
                int middlePostCnt =0;

                for(Category category : middleCategory.getCategoryList()){
                    int postCnt = category.getPosts().size();
                    categoryRepository.updatePostCnt(category.getId(), postCnt);
                    middlePostCnt+=postCnt;
                }

                middleCategoryRepository.updatePostCnt(middleCategory.getId(), middlePostCnt);
                mainPostCnt+=middlePostCnt;
            }

            mainCategoryRepository.updatePostCnt(mainCategory.getId(), mainPostCnt);
        }
    }

    @Transactional
    public void deleteCategory(long id){

        // 이게 jpa에서 category를 지우는 거랑, java에서 middleCategory 안의 categoryList 안에서 category를 지우는 게 다르다.
        // jpa에서만 지우면 middleCategory에 categoryList에는 category가 남아있다.

        Category category = categoryRepository.findById(id);
        int deletedPostCnt = category.getPostCnt();

        MiddleCategory middleCategory = category.getMiddleCategory();
        MainCategory mainCategory = middleCategory.getMainCategory();
        middleCategory.getCategoryList().remove(category);

        mainCategoryRepository.updatePostCnt(mainCategory.getId(), mainCategory.getPostCnt()-deletedPostCnt);
        middleCategoryRepository.updatePostCnt(middleCategory.getId(), middleCategory.getPostCnt()-deletedPostCnt);
        categoryRepository.deleteById(id);

    }
}
