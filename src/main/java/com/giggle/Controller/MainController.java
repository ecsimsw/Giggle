package com.giggle.Controller;

import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;

    @RequestMapping("/")
    @ResponseBody
    public String hello(){ return "hi"; }


    @RequestMapping("/board")
    @ResponseBody
    public String board(@SessionAttribute String loginId){
        return loginId;
    }

}
