package com.giggle.Repository;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final EntityManager em;

    public void save(Category category){
        em.persist(category);
    }

    public List<Category> findAllCategory(){
        List<Category> selectedCategories= em.createQuery("select c from Category c", Category.class)
                .getResultList();
        return selectedCategories;
    }
}
