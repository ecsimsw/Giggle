package com.giggle.Controller;

import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Message.EjoinMessage;
import com.giggle.Message.EloginMessage;
import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String login() { return "loginForm"; }

    @PostMapping("/login")
    public String login(LoginForm loginForm,
                        HttpSession session,
                        RedirectAttributes redirectAttributes)
    {
        EloginMessage resultMessage = memberService.loginCheck(loginForm);

        if(resultMessage == EloginMessage.success){
            redirectAttributes.addFlashAttribute("message", "login_success");
            session.setAttribute("loginId", loginForm.getLoginId());
            return "redirect:/board";
        }
        else if(resultMessage == EloginMessage.nonExistLoginId){
            redirectAttributes.addFlashAttribute("message", "non-existent users");
            return "redirect:/login";
        }
        else if(resultMessage == EloginMessage.wrongLoginPw){
            redirectAttributes.addFlashAttribute("message", "wrong password");
            return "redirect:/login";
        }
        throw new RuntimeException();
    }

    @GetMapping("/join")
    public String join(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(JoinForm joinForm, Model model, RedirectAttributes redirectAttributes){
        EjoinMessage resultMessage = memberService.join(joinForm);
        model.addAttribute("joinForm", joinForm);
        if(resultMessage == EjoinMessage.loginIdDuplicate){
            redirectAttributes.addFlashAttribute("message", "Duplicated loginId");
            return "redirect:/join";
        }
        else if(resultMessage == EjoinMessage.nickNameDuplicate){
            redirectAttributes.addFlashAttribute("message", "Duplicated nickName");
            return "redirect:/join";
        }
        else if(resultMessage == EjoinMessage.success){
            model.addAttribute("Member", joinForm.getNickName());
            return "test";  // after joinPage
        }
        throw new RuntimeException();
    }
}
