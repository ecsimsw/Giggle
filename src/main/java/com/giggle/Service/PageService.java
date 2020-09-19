package com.giggle.Service;

import com.giggle.Domain.Entity.*;
import com.giggle.Repository.PageRepository;
import com.giggle.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private final PostRepository postRepository;


    /// edit Main img Board

    @Transactional
    public void addImgBoard(MultipartFile[] multipartFiles, String resourceSrc) throws IOException {
        for(MultipartFile file : multipartFiles){
            if(file != null) {
                String sourceFileName = file.getOriginalFilename();
                String sourceFileExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
                String fileName = sourceFileName + "." + sourceFileExtension;
                File destFile = new File(resourceSrc +"/"+ fileName);
                file.transferTo(destFile);
                pageRepository.createMainBoardImg(fileName);
            }
        }
    }

    public List<String> getMainBoardImgSrc(String basePath){
        List<String> imgSrcList = new ArrayList<>();

        List images = pageRepository.getMainBoardImages();
        if(images != null){
            for(MainBoardImg imgs : pageRepository.getMainBoardImages()){
                imgSrcList.add(basePath+"/"+imgs.getFileName());
            }
        }
        return imgSrcList;
    }

    public List<MainBoardImg> getAllMainBoardImg(){
        return pageRepository.getMainBoardImages();
    }

    @Transactional
    public void deleteImgArr(long[] idArr, String basePath){
        for(long id : idArr){
            MainBoardImg mainBoardImg = pageRepository.findMainBoardImgById(id);
            File deleteFile = new File(basePath+"/"+mainBoardImg.getFileName());
            if(deleteFile.exists()) { deleteFile.delete(); }
            pageRepository.deleteMainBoardImg(mainBoardImg);
        }
    }


    /// edit shortCuts

    @Transactional
    public void addShortCut(Category category, String description, String color){

        pageRepository.createShortCut(category.getName(), category.getId(), description, color);
    }

    @Transactional
    public void deleteShortCutArr(long[] idArr){
        ShortCut shortCut;
        for(long id : idArr){
            shortCut = pageRepository.findShortCutById(id);
            pageRepository.deleteShortCut(shortCut);
        }
    }

    public List<ShortCut> getAllShortCut(){
        return pageRepository.getAllShortCut();
    }


    // edit dashBoard

    public DashBoard findDashBoardById(long id){
        return pageRepository.findDashBoardById(id);
    }

    @Transactional
    public void addDashBoard(DashBoardType type, String width, String height){
        pageRepository.createDashBoard(type, width, height);
        this.updateSpotType();
    }

    @Transactional
    public void deleteDashBoard(long id){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        pageRepository.deleteDashBoard(dashBoard);
        this.updateSpotType();
    }

    public List<DashBoard> getAllDashBoard(){
        return pageRepository.getAllDashBoard();
    }

    public void editDashBoardType(long id, DashBoardType type, String width, String height){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        pageRepository.updateDashBoard(dashBoard, type, width, height);
        this.updateSpotType();
    }

    public void editDashBoardFreePost(long id, String title, String content){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        pageRepository.updateDashBoardTitle(dashBoard, title);
        pageRepository.updateDashBoardContent(dashBoard, content.replace("\r\n", "<br>"));
    }

    public void editDashBoardLinkPost(long id, String title, long linkId){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        pageRepository.updateDashBoardTitle(dashBoard, title);

        Post linkedPost = postRepository.findById(linkId);
        pageRepository.updateDashBoardContent(dashBoard, linkedPost.getContent());
    }

    public void editDashBoardLatestPost(long id, String title, long linkId){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        pageRepository.updateDashBoardTitle(dashBoard, title);
        pageRepository.updateDashBoardLinkId(dashBoard, linkId);
    }

    public void updateSpotType(){
        List<DashBoard> dashBoardList = this.getAllDashBoard();
        log.info(String.valueOf(dashBoardList.size()));

        DashBoard now;
        DashBoard prev;
        DashBoard next;

        int size = dashBoardList.size();
        int shortStack =0;
        int longStack =0;

        for(int i=0; i<size; i++){
            now = dashBoardList.get(i);
            if(now.getWidth().equals("wide")){
                pageRepository.updateSpotType(now, 0);
                shortStack =0;
                longStack =0;
            }
            else if(now.getWidth().equals("narrow")){
                if(now.getHeight().equals("short")){
                    if(longStack ==0){
                        pageRepository.updateSpotType(now, 0);
                        shortStack++;
                        if(shortStack == 4) shortStack=0;
                    }
                    else if(longStack == 1) {
                        if(i<size-1){
                            next = dashBoardList.get(i+1);
                            if(next.getWidth().equals("narrow") && next.getHeight().equals("short")){
                                pageRepository.updateSpotType(now, 1);
                                pageRepository.updateSpotType(next, 2);
                                i++;
                            }
                            else{pageRepository.updateSpotType(now, 0); }
                        }
                        else{pageRepository.updateSpotType(now, 0); }
                        longStack=0;
                        shortStack =0;
                    }
                }
                else if(now.getHeight().equals("long")){
                    if(shortStack ==0){
                        pageRepository.updateSpotType(now, 0);
                        longStack++;
                        if(longStack == 2){ longStack =0; }
                    }
                    else if(shortStack == 1){
                        pageRepository.updateSpotType(now, 0);
                        longStack =0;
                        shortStack =0;
                    }
                    else if(shortStack == 2){
                        pageRepository.updateSpotType(now, 0);
                        prev = dashBoardList.get(i-1);
                        pageRepository.updateSpotType(prev, 2);
                        prev = dashBoardList.get(i-2);
                        pageRepository.updateSpotType(prev, 1);
                        longStack =0;
                        shortStack =0;
                    }
                    else if(shortStack == 3){
                        pageRepository.updateSpotType(now, 0);
                        shortStack=0;
                        longStack=1;
                    }
                }
            }

        }

    }
}
