package com.giggle.Repository;

import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Domain.Entity.MiddleCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PageRepository {

    private final EntityManager em;

    public void createMainBoardImg(String fileName){
        MainBoardImg newImg = new MainBoardImg();
        newImg.setFileName(fileName);
        em.persist(newImg);
    }

    public List<MainBoardImg> getMainBoardImages(){
        List<MainBoardImg> mainBoardImgSrc = em.createQuery("select img from MainBoardImg img", MainBoardImg.class)
                .getResultList();
        return mainBoardImgSrc;
    }

    public MainBoardImg findById(long id){
        return em.find(MainBoardImg.class, id);
    }

    public void deleteMainBoard(MainBoardImg mainBoardImg){
        em.remove(mainBoardImg);
    }
}
