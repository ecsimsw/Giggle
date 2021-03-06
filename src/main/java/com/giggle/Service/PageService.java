package com.giggle.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.giggle.Domain.Entity.*;
import com.giggle.Repository.PageRepository;
import com.giggle.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private final S3Service s3Service;

    /// edit Main img Board
    @Transactional
    public void addImgBoard(MultipartFile[] multipartFiles, String resourceSrc) throws IOException {
        for(MultipartFile file : multipartFiles){
            if(file != null) {
                String sourceFileName = file.getOriginalFilename();
                String fileName = sourceFileName;
                File destFile = new File(resourceSrc +"/"+ fileName);
                file.transferTo(destFile);
                pageRepository.createMainBoardImg(fileName);
            }
        }
    }

    @Transactional
    public void addImgBoardWithS3(MultipartFile[] multipartFiles, String basePath) throws IOException {
        for(MultipartFile file : multipartFiles){
            if(file != null) {
                // 현재 사진 파일 S3 저장
                String sourceFileName = file.getOriginalFilename();

                // 현재 날짜, 시간을 기준으로 구별값 첨부 -> 중복 방지
                int dateTimeInteger = (int) (new Date().getTime()/1000);
                String fileName = dateTimeInteger+sourceFileName;
                log.info("fileName : " + fileName);

                s3Service.upload(file, basePath, fileName);

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

    @Transactional
    public void deleteImgArrWithS3(long[] idArr, String basePath){
        for(long id : idArr){
            MainBoardImg mainBoardImg = pageRepository.findMainBoardImgById(id);

            String filePath = basePath+"/"+mainBoardImg.getFileName();

            s3Service.delete(filePath);

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
    public long addDashBoard(DashBoardType type, String width, String height){
        long dashBoardId = pageRepository.createDashBoard(type, width, height);
        this.updateSpotType();
        return dashBoardId;
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
        dashBoard.setType(type);
        dashBoard.setWidth(width);
        dashBoard.setHeight(height);
        this.updateSpotType();
    }

    @Transactional
    public void editDashBoardFreePost(long id, String title, String content){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        dashBoard.setTitle(title);
        dashBoard.setContent(content.replace("\r\n", "<br>"));
    }

    @Transactional
    public void editDashBoardLinkPost(long id, String title, long linkId){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        dashBoard.setTitle(title);
        dashBoard.setLinkId(linkId);
    }

    @Transactional
    public void editDashBoardLatestPost(long id, String title, long linkId){
        DashBoard dashBoard = pageRepository.findDashBoardById(id);
        dashBoard.setTitle(title);
        dashBoard.setLinkId(linkId);
    }

    @Transactional
    public void updateSpotType(){
        List<DashBoard> dashBoardList = this.getAllDashBoard();

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
