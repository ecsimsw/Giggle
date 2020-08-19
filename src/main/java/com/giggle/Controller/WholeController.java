package com.giggle.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WholeController {

//    @ExceptionHandler(RuntimeException.class)
//    public String runtimeExceptionHandler(RuntimeException re, Model model){
//        model.addAttribute("message", re.getMessage());
//        return "runtimeError";
//    }
}
