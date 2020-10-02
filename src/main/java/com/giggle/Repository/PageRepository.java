package com.giggle.Repository;

import com.giggle.Domain.Entity.DashBoard;
import com.giggle.Domain.Entity.DashBoardType;
import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Domain.Entity.ShortCut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void createShortCut(String categoryName, long categoryId, String description, String color){
        ShortCut newShortCut = new ShortCut();
        newShortCut.setTitle(categoryName);
        newShortCut.setCategoryId(categoryId);
        newShortCut.setDescription(description);
        newShortCut.setColor(color);
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

    // edit dashBoard
    public DashBoard findDashBoardById(long id){return em.find(DashBoard.class, id);}

    public long createDashBoard(DashBoardType type, String width, String height){
        DashBoard newDashBoard = new DashBoard();
        newDashBoard.setType(type);
        newDashBoard.setHeight(height);
        newDashBoard.setWidth(width);
        newDashBoard.setSpotType(0);
        em.persist(newDashBoard);

        return newDashBoard.getId();
    }

    public List<DashBoard> getAllDashBoard(){
        List<DashBoard> allDashBoard = em.createQuery("select d from DashBoard d", DashBoard.class)
                .getResultList();
        return allDashBoard;
    }

    public void deleteDashBoard(DashBoard dashBoard){
        em.remove(dashBoard);
    }

    public void updateSpotType(DashBoard dashBoard, int type){
        dashBoard.setSpotType(type);
        em.flush();
    }
}
