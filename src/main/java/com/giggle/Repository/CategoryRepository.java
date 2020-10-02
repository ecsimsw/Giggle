package com.giggle.Repository;

import com.giggle.Domain.Entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final EntityManager em;

    public void save(Category category){
        if(category.getId()==null){
            em.persist(category);
        }else{
            em.merge(category);
        }
    }

    public Category findById(Long id){
        return em.find(Category.class,id);
    }

    public void updatePostCnt(long categoryId, int postCnt){
        Category category = findById(categoryId);
        category.setPostCnt(postCnt);
    }

    public void deleteById(long id){
        em.remove(findById(id));
    }
}
