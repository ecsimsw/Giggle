package com.giggle.Repository;

import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.MiddleCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MiddleCategoryRepository {

    private final EntityManager em;

    public void save(MiddleCategory middleCategory){
        em.persist(middleCategory);
    }

    public List<MiddleCategory> findAllMiddleCategory(){
        List<MiddleCategory> middleCategories= em.createQuery("select mc from MiddleCategory mc", MiddleCategory.class)
                .getResultList();
        return middleCategories;
    }

    public MiddleCategory findById(Long id){
        return em.find(MiddleCategory.class, id);
    }
}
