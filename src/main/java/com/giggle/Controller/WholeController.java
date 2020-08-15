package com.giggle.Controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WholeController {

    @ExceptionHandler(RuntimeException.class)
    public String runtimeExceptionHandler(){
        return "RuntimeError";
    }
}
