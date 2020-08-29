package com.giggle.Repository;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.CommunityType;
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

    public List<Category> findAllCategoryInCommunity(CommunityType communityType){
        List<Category> selectedCategories= em.createQuery("select c from Category c where c.communityType =: communityType", Category.class)
                .setParameter("communityType", communityType)
                .getResultList();
        return selectedCategories;
    }

    public Category getCategoryByName(CommunityType communityType, String categoryName){
        Category selectedCategory = em.createQuery("select c from Category c where c.name =:categoryName AND c.communityType = :communityType",Category.class)
                .setParameter("communityType", communityType)
                .setParameter("categoryName",categoryName)
                .getResultList()
                .get(0);
        return selectedCategory;
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
