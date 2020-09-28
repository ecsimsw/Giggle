package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Validator.CheckAuthority;
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
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    @Autowired JoinValidator joinValidator;

    @Autowired CheckAuthority checkAuthority;

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
            Member loginMember = memberService.getByLoginId(loginForm.getLoginId());
            session.setAttribute("loginId", loginMember.getLoginId());
            session.setAttribute("authority", loginMember.getMemberType().name());
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
        session.removeAttribute("authority");
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
            return "redirect:/member/join";
        }
        else if(resultMessage == EjoinMessage.nickNameDuplicate){
            redirectAttributes.addFlashAttribute("message", "Duplicated nickName");
            return "redirect:/member/join";
        }
        else if(resultMessage == EjoinMessage.success){
            Member loginMember = memberService.getByLoginId(joinForm.getLoginId());
            session.setAttribute("loginId", loginMember.getLoginId());
            session.setAttribute("authority", loginMember.getMemberType().name());
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

    @PostMapping("/manage/search")
    @ResponseBody
    public String search(String loginId, String userName) throws JsonProcessingException {

        String result = "";

        log.info(loginId);

        if(loginId != null){
            Member member = memberService.getByLoginId(loginId);

            if(member == null){
                result = null;
            }
            else{
                result = objectMapper.writeValueAsString(member);
            }
        }
        else if(userName != null) {
            Member member = memberService.getByName(userName);

            if(member == null){
                result = null;
            }
            else{
                result = objectMapper.writeValueAsString(member);
            }
        }
        return result;
    }

    @GetMapping("/manage/setting")
    public String manageSetting(Model model, HttpSession httpSession){

        String authority = checkAuthority.checkAuthority(httpSession);

        if(!authority.equals("master")){
            throw new RuntimeException("You do not have access rights.");
        }

        List<Member> memberList = memberService.getAllMember();
        model.addAttribute("memberList", memberList);

        return "userManagement";
    }

    @PostMapping("/manage/update")
    public String manageUpdate(MemberInfo memberInfo, HttpSession httpSession) throws JsonProcessingException {

        String authority = checkAuthority.checkAuthority(httpSession);

        if(!authority.equals("master")){
            throw new RuntimeException("You do not have access rights.");
        }

        if(memberInfo.getId() != null){
            long id = Long.parseLong(memberInfo.getId());
            memberService.updateMemberInfo(id, memberInfo);
        }
        else{
            throw new RuntimeException("no-id existence");
        }

        return "redirect:/member/manage/setting";
    }

    @GetMapping("/manage/delete")
    public String manageDelete(@RequestParam("id") String idStr, HttpSession httpSession) throws JsonProcessingException {

        String authority = checkAuthority.checkAuthority(httpSession);

        if(!authority.equals("master")){
            throw new RuntimeException("You do not have access rights.");
        }

        if(idStr != null){
            long id = Long.parseLong(idStr);
            memberService.deleteById(id);
        }
        else{
            throw new RuntimeException("no-id existence");
        }

        return "redirect:/member/manage/setting";
    }
}
