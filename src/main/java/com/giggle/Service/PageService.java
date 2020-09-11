package com.giggle.Service;

import com.giggle.Repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
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

    public void addImgBoard(List<String> imgSrcList) throws IOException {
        for(String fileSrc : imgSrcList){

            FileInputStream fis = new FileInputStream(fileSrc);


            String sourceFileName = fileSrc.substring(fileSrc.lastIndexOf("."));
            String sourceFileExtension =  fileSrc.substring(fileSrc.lastIndexOf(".")+1).toLowerCase();

            FileOutputStream fileInServer = new FileOutputStream("static\\file\\mainBoardImg\\"+sourceFileName+sourceFileExtension);


            while(true){
                int data = fis.read();
                if(data == -1){
                    break;
                }
                fileInServer.write(data);
            }

            fis.close();
            fileInServer.close();
        }

    }
}
