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
        em.persist(category);
    }

    public Category findById(Long id){
        return em.find(Category.class,id);
    }

    public void updatePostCnt(Category category, int postCnt){
//        Category c = getCategoryById(category.getId());  다시 찾을 필요 없음.
        category.setPostCnt(postCnt);
//        em.merge(category);   필요 없음. 영속성 컨텍스트 / set으로 처리
    }

    public void deleteById(long id){
        em.remove(findById(id));
    }
}
