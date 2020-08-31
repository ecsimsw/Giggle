package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.MiddleCategory;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Repository.CategoryRepository;
import com.giggle.Repository.MainCategoryRepository;
import com.giggle.Repository.MiddleCategoryRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MiddleCategoryRepository middleCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final PostRepository postRepository;

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

    public void updatePostCnt(Category category, int postCnt){
        int dif = postCnt - category.getPostCnt();
        categoryRepository.updatePostCnt(category, postCnt);

        MiddleCategory middleCategory = category.getMiddleCategory();
        middleCategoryRepository.updatePostCnt(middleCategory, middleCategory.getPostCnt()+dif);

        MainCategory mainCategory = middleCategory.getMainCategory();
        mainCategoryRepository.updatePostCnt(mainCategory, mainCategory.getPostCnt()+dif);
    }

    public void updateWholeCategoryPostCnt(){
        List<MainCategory> allMainCategory = mainCategoryRepository.findAllMainCategory();

        for(MainCategory mainCategory : allMainCategory){
            int mainPostCnt =0;

            for(MiddleCategory middleCategory : mainCategory.getMiddleCategoryList()){
                int middlePostCnt =0;

                for(Category category : middleCategory.getCategoryList()){
                    int postCnt = category.getPosts().size();
                    categoryRepository.updatePostCnt(category, postCnt);
                    middlePostCnt+=postCnt;
                }

                middleCategoryRepository.updatePostCnt(middleCategory, middlePostCnt);
                mainPostCnt+=middlePostCnt;
            }

            mainCategoryRepository.updatePostCnt(mainCategory, mainPostCnt);
        }
    }

    public void updateAllCategoryPostCnt(){
        List<MainCategory> allMainCategory = mainCategoryRepository.findAllMainCategory();

        for(MainCategory mainCategory : allMainCategory){
            int mainPostCnt =0;

            for(MiddleCategory middleCategory : mainCategory.getMiddleCategoryList()){
                int middlePostCnt =0;

                for(Category category : middleCategory.getCategoryList()){
                    int postCnt = category.getPostCnt();
                    middlePostCnt+=postCnt;
                    mainPostCnt+=postCnt;
                }
                middleCategoryRepository.updatePostCnt(middleCategory, middlePostCnt);
            }

            mainCategoryRepository.updatePostCnt(mainCategory, mainPostCnt);
        }
    }

    public List<Post> getPostsInCategory(Category category, int page, int postForPage){
        int totalCnt = category.getPostCnt();

        int from;
        int max;

        if((totalCnt-(page * postForPage))>=0){
            from = totalCnt-(page * postForPage);
            max = postForPage;
        }
        else{
            from = 0;
            max = totalCnt % postForPage;
        }

        return postRepository.postInCommunityCategory(category, from, max);
    }

    @Transactional
    public void deleteCategory(long id){

        // 이게 jpa에서 category를 지우는 거랑, java에서 middleCategory 안의 categoryList 안에서 category를 지우는 게 다르다.

        // jpa에서만 지우면 middleCategory에 categoryList에는 category가 남아있다.

        Category category = categoryRepository.findById(id);
        int postCnt = category.getPostCnt();

        MiddleCategory middleCategory = category.getMiddleCategory();
        MainCategory mainCategory = middleCategory.getMainCategory();
        middleCategory.getCategoryList().remove(category);

        mainCategoryRepository.updatePostCnt(mainCategory, mainCategory.getPostCnt()-postCnt);
        middleCategoryRepository.updatePostCnt(middleCategory, middleCategory.getPostCnt()-postCnt);
        categoryRepository.updatePostCnt(category, 0);

        categoryRepository.deleteById(id);

    }
}
