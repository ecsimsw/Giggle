package com.giggle.Controller;

import com.giggle.Entity.JoinForm;
import com.giggle.Entity.LoginForm;
import com.giggle.Entity.Member;
import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String login(Model model)
    {
        String message = (String)model.asMap().get("message");

        return "loginForm";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, Model model, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("loginForm", loginForm);
        return "redirect:/loginCheck";
    }

    @GetMapping("/loginCheck")
    public String loginCheck(Model model, RedirectAttributes redirectAttributes, HttpSession session)
    {
        LoginForm loginForm = (LoginForm) model.asMap().get("loginForm");
        boolean result = memberService.loginCheck(loginForm);

        if(result){
            redirectAttributes.addAttribute("message", "login success");
            session.setAttribute("loginId", loginForm.getLoginId());
            return "redirect:/board";
        }
        else{
            redirectAttributes.addAttribute("message", "login failed");
            return "redirect:/login";
        }
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
