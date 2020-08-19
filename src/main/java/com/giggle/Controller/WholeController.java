package com.giggle.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WholeController {

    @ExceptionHandler(RuntimeException.class)
    public String runtimeExceptionHandler(RuntimeException d, Model model){
        model.addAttribute("message", d.getMessage());
        return "runtimeError";
    }
}
