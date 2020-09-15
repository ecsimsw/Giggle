package com.giggle.Repository;

import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Domain.Entity.MiddleCategory;
import com.giggle.Domain.Entity.ShortCut;
import com.giggle.Domain.Form.ShortCutForm;
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


    // edit main board img

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

    public MainBoardImg findMainBoardImgById(long id){
        return em.find(MainBoardImg.class, id);
    }

    public void deleteMainBoardImg(MainBoardImg mainBoardImg){
        em.remove(mainBoardImg);
    }


    /// edit shortCut

    public ShortCut findShortCutById(long id){return em.find(ShortCut.class, id);}

    public void createShortCut(ShortCutForm shortCutForm){
        ShortCut newShortCut = new ShortCut();
        newShortCut.setTitle(shortCutForm.getTitle());
        newShortCut.setDescription(shortCutForm.getDescription());
        newShortCut.setLink(shortCutForm.getLink());
        em.persist(newShortCut);
    }

    public List<ShortCut> getAllShortCut(){
        List<ShortCut> allShortCut = em.createQuery("select s from ShortCut s", ShortCut.class)
                .getResultList();
        return allShortCut;
    }

    public void deleteShortCut(ShortCut shortCut){
        em.remove(shortCut);
    }
}
