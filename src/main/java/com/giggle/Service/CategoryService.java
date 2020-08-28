package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.Post;
import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Repository.CategoryRepository;
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
        categoryRepository.updatePostCnt(category, postCnt);
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
        categoryRepository.deleteById(id);
    }
}
