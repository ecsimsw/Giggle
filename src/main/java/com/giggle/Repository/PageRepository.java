package com.giggle.Repository;

import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Domain.Entity.MiddleCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PageRepository {
    private EntityManager em;

    public void createMainBoardImg(String fileName){
        MainBoardImg newImg = new MainBoardImg();
        newImg.setFileName(fileName);
        em.persist(newImg);
    }

    public List<MainBoardImg> getMainBoardImages(){
        List<MainBoardImg> mainBoardImgSrc= em.createQuery("select img from MainBoardImg img", MainBoardImg.class)
                .getResultList();
        return mainBoardImgSrc;
    }

}
