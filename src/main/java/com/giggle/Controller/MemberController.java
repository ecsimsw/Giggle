package com.giggle.Controller;

import com.giggle.Entity.JoinForm;
import com.giggle.Entity.LoginForm;
import com.giggle.Entity.Member;
import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String login(){
        return "loginForm";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, Model model){
        model.addAttribute("Member", loginForm.getLoginId());
        return "test";

        // redirect after Login Form
    }

    @GetMapping("/join")
    public String join(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute JoinForm joinForm, Model model){
        Member member = new Member();
        member.setLoginId(joinForm.getLoginId());
        member.setLoginPw(joinForm.getLoginPw());
        member.setName(joinForm.getName());
        member.setNickName(joinForm.getNickName());
        memberService.join(member);

        model.addAttribute("Member", member);
        return "test";

        // redirect after join Form
    }
}
