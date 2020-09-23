package com.giggle.Controller;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Validator.JoinValidator;
import com.giggle.Validator.Message.EjoinMessage;
import com.giggle.Validator.Message.EloginMessage;
import com.giggle.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Autowired
    JoinValidator joinValidator;

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
            return "redirect:/main";
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginId");
        return "redirect:/main";
    }

    @GetMapping("/join")
    public String join(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, Model model, HttpSession session,
                       RedirectAttributes redirectAttributes,
                       BindingResult bindingResult){

        model.addAttribute("joinForm", joinForm);

        joinValidator.validate(joinForm, bindingResult);

        if(bindingResult.hasErrors()){
            throw new RuntimeException("Invalid joinForm");
        }

        EjoinMessage resultMessage = memberService.join(joinForm);

        if(resultMessage == EjoinMessage.loginIdDuplicate){
            redirectAttributes.addFlashAttribute("message", "Duplicated loginId");
            return "redirect:/join";
        }
        else if(resultMessage == EjoinMessage.nickNameDuplicate){
            redirectAttributes.addFlashAttribute("message", "Duplicated nickName");
            return "redirect:/join";
        }
        else if(resultMessage == EjoinMessage.success){
            session.setAttribute("loginId", joinForm.getLoginId());
            return "redirect:/main";  // after joinPage
        }
        throw new RuntimeException();
    }

    @GetMapping("/setting")
    public String setting(Model model, HttpSession session){
        String loginId = (String)session.getAttribute("loginId");

        Member member = memberService.getByLoginId(loginId);
        model.addAttribute("member", member);
        return "setMember";
    }

    @PostMapping("/setting/memberInfo")
    public String settingMemberInfo(MemberInfo memberInfo, HttpSession session){

        String loginId = (String)session.getAttribute("loginId");

        long id = memberService.getByLoginId(loginId).getId();
        Member member = memberService.updateMemberInfo(id, memberInfo);

        return "redirect:/main";
    }
}
