package com.giggle.Controller;

import com.giggle.S3Service;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
}
