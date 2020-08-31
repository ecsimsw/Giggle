package com.giggle.Repository;

import com.giggle.Domain.Entity.MainCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MainCategoryRepository {

    private final EntityManager em;

    public void save(MainCategory mainCategory){
        em.persist(mainCategory);
    }

    public List<MainCategory> findAllMainCategory(){
        List<MainCategory> mainCategories= em.createQuery("select mc from MainCategory mc", MainCategory.class)
                .getResultList();
        return mainCategories;
    }

    public MainCategory findById(Long id){
        return em.find(MainCategory.class, id);
    }

    public void updatePostCnt(MainCategory mainCategory, int postCnt){
        mainCategory.setPostCnt(postCnt);
//        em.merge(mainCategory);
    }

    public void deleteById(long id){
        em.remove(findById(id));
    }
}
