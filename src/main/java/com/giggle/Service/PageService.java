package com.giggle.Service;

import com.giggle.Repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ResourceLoader resourceLoader;

    public void addImgBoard(MultipartFile[] multipartFiles, String resourceSrc) throws IOException {
        for(MultipartFile file : multipartFiles){
            if(file != null) {
                String sourceFileName = file.getOriginalFilename();
                String sourceFileExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

                File destFile;


                destFile = new File(resourceSrc +"\\"+ sourceFileName + "." + sourceFileExtension);
                file.transferTo(destFile);
            }
        }
    }
}
