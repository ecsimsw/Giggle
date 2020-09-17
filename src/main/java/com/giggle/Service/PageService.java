package com.giggle.Service;

import com.giggle.Domain.Entity.Category;
import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Domain.Entity.ShortCut;
import com.giggle.Repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;


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
}
