package com.giggle.Service;

import com.giggle.Domain.Entity.MainBoardImg;
import com.giggle.Repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ResourceLoader;
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
            MainBoardImg mainBoardImg = pageRepository.findById(id);

            File deleteFile = new File(basePath+"/"+mainBoardImg.getFileName());

            if(deleteFile.exists()) { deleteFile.delete(); }

            pageRepository.deleteMainBoard(mainBoardImg);
        }
    }
}
