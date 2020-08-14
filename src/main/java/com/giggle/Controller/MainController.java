package com.giggle.Controller;

import com.giggle.Entity.LoginForm;
import com.giggle.Entity.Member;
import com.giggle.Repository.MemberRepository;
import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
