package com.giggle.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Controller
@RequiredArgsConstructor
public class WholeController {

//    @ExceptionHandler(RuntimeException.class)
//    public String runtimeExceptionHandler(RuntimeException re, Model model){
//        model.addAttribute("message", re.getMessage());
//        return "runtimeError";
//    }


//    @GetMapping("/test")
//    public List<String> uploadImage(@RequestPart List<MultipartFile> files) throws Exception{
//        List<String> list = new ArrayList<>();
//        for(MultipartFile file : files){
//            file.transferTo();
//        }
//    }
}
