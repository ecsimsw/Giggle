package com.giggle.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.MemberType;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Validation.CheckAuthority;
import com.giggle.Validation.JoinValidator;
import com.giggle.Validation.Message.EjoinMessage;
import com.giggle.Validation.Message.EloginMessage;
import com.giggle.Service.MemberService;
import com.giggle.Validation.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    private final JoinValidator joinValidator;
    private final CheckAuthority checkAuthority;

    @GetMapping("/login")
    public String login() { return "loginForm"; }

    @PostMapping("/login")
    public String login(LoginForm loginForm,
                        HttpSession session,
                        RedirectAttributes redirectAttributes)
    {
        EloginMessage resultMessage = memberService.loginCheck(loginForm);

        if(resultMessage == EloginMessage.success){
            Member loginMember = memberService.getByLoginId(loginForm.getLoginId());
            session.setAttribute("loginId", loginMember.getLoginId());
            session.setAttribute("authority", loginMember.getMemberType().name());

            String dest = (String)session.getAttribute("dest");
            String redirect = (dest == null) ? "/main" : dest;
            return "redirect:"+redirect;
        }
        else if(resultMessage == EloginMessage.nonExistLoginId){
            redirectAttributes.addFlashAttribute("message", "non-existent users");
            return "redirect:/member/login";
        }
        else if(resultMessage == EloginMessage.wrongLoginPw){
            redirectAttributes.addFlashAttribute("message", "wrong password");
            return "redirect:/member/login";
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

        String email = joinForm.getEmail();
        String enteredKey = joinForm.getEmailCheck();

        if(!memberService.verifyEmail(email, enteredKey)){
            // 인증 번호가 틀릴 경우
            redirectAttributes.addFlashAttribute("message", "이메일 인증 번호 불일치");
            redirectAttributes.addFlashAttribute("joinForm", joinForm);
            return "redirect:/member/join";
        };

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
            Member joinMember = memberService.getByLoginId(joinForm.getLoginId());

            session.setAttribute("loginId", joinMember.getLoginId());
            session.setAttribute("authority", joinMember.getMemberType());

            return "redirect:/main";  // after joinPage
        }
        throw new RuntimeException();
    }

    @PostMapping("/join/checkIdDuplicate")
    @ResponseBody
    public String checkIdDuplicate(String loginId) throws JsonProcessingException {
        Member member = memberService.getByLoginId(loginId);

        if(member == null){ return objectMapper.writeValueAsString("ok"); }
        else{ return objectMapper.writeValueAsString("duplicate"); }
    }

    @PostMapping("/join/checkNameDuplicate")
    @ResponseBody
    public String checkNameDuplicate(String name) throws JsonProcessingException {
        Member member = memberService.getByName(name);

        if(member == null){ return objectMapper.writeValueAsString("ok"); }
        else{ return objectMapper.writeValueAsString("duplicate"); }
    }

    @PostMapping("/join/sendAuthMail")
    @ResponseBody
    public String authenticationMail(String email) throws JsonProcessingException{
        Member member = memberService.getByEmail(email);
        String msg = "";

        if(member != null){
            msg = "duplicate";
            msg = objectMapper.writeValueAsString(msg);
            return msg;
        }

        memberService.sendAuthMail(email);
        msg = "success";
        msg = objectMapper.writeValueAsString(msg);
        return msg;
    }

    @GetMapping("/setting")
    public String setting(Model model, HttpSession session){
        String loginId = (String)session.getAttribute("loginId");

        Member member = memberService.getByLoginId(loginId);
        model.addAttribute("member", member);
        return "userSetting";
    }

    // ---- integration AWS/s3
    @PostMapping("/setting/profileImg")
    public String updateProfileImg(long id, MultipartFile profileImg) throws IOException {
        String basePath = "profile";
        String fileName = profileImg.getOriginalFilename();

        if (profileImg.isEmpty()) return "redirect:/member/setting";
        if (fileName.equals("stranger.png") || fileName.equals("default.png")) {
            throw new RuntimeException("Invalid file name");
        }

        memberService.addProfileImgWithS3(profileImg,basePath,id);

        return "redirect:/member/setting";
    }

//    ---- in window
//    @PostMapping("/setting/profileImg")
//    public String updateProfileImg(long id, HttpServletRequest httpServletRequest, MultipartFile profileImg) throws IOException {
//        String basePath = httpServletRequest.getServletContext().getRealPath("/profile");
//        String fileName = profileImg.getOriginalFilename();
//
//        if(fileName.equals("stranger.png") || fileName.equals("default.png")) {
//            throw new RuntimeException("Invalid file name");
//        }
//        memberService.addProfileImg(profileImg,basePath,id);
//
//        return "redirect:/member/setting";
//    }

    @PostMapping("/setting/memberInfo")
    public String settingMemberInfo(MemberInfo memberInfo, Model model,  HttpSession session){

        String loginId = (String)session.getAttribute("loginId");

        long id = memberService.getByLoginId(loginId).getId();

        if(memberService.getByName(memberInfo.getName()) == null){
            memberService.updateMemberInfo(id, memberInfo);
            session.removeAttribute("loginId");
            session.removeAttribute("authority");
            return "redirect:/main";
        }
        else{
            model.addAttribute("message", "Entered name is already used");
        }
        return "redirect:/member/setting";
    }

    @Permission(authority = MemberType.master)
    @PostMapping("/manage/update")
    public String manageUpdate(MemberInfo memberInfo, HttpSession httpSession) throws JsonProcessingException {

        String authority = checkAuthority.checkAuthority(httpSession);

        if(!authority.equals("master")){ throw new RuntimeException("You do not have access rights."); }

        if(memberInfo.getId() != null){
            long id = Long.parseLong(memberInfo.getId());
            memberService.updateMemberInfo(id, memberInfo);
        }
        else{ throw new RuntimeException("no-id existence"); }

        return "redirect:/member/manage/setting";
    }

    @PostMapping("/manage/search")
    @ResponseBody
    public String search(String loginId, String userName) throws JsonProcessingException {

        String result = "";

        if(loginId != null){
            Member member = memberService.getByLoginId(loginId);
            if(member == null){
                result = "none";
                result = objectMapper.writeValueAsString(result);
            }
            else{
                MemberInfo searchedMember = new MemberInfo();
                searchedMember.setId(member.getId().toString());
                searchedMember.setEmail(member.getEmail());
                searchedMember.setName(member.getName());
                searchedMember.setType(member.getMemberType().name());
                searchedMember.setLoginId(member.getLoginId());
                result = objectMapper.writeValueAsString(searchedMember);
            }
        }
        else if(userName != null) {
            Member member = memberService.getByName(userName);
            if(member == null){
                result = "none";
                result = objectMapper.writeValueAsString(result);
            }
            else{
                MemberInfo searchedMember = new MemberInfo();
                searchedMember.setId(member.getId().toString());
                searchedMember.setEmail(member.getEmail());
                searchedMember.setName(member.getName());
                searchedMember.setType(member.getMemberType().name());
                searchedMember.setLoginId(member.getLoginId());
                result = objectMapper.writeValueAsString(searchedMember);
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

    @Permission(authority = MemberType.master)
    @GetMapping("/manage/delete")
    public String manageDelete(@RequestParam("id") String idStr, HttpSession httpSession) throws JsonProcessingException {

        String authority = checkAuthority.checkAuthority(httpSession);

        if(!authority.equals("master")){ throw new RuntimeException("You do not have access rights."); }

        if(idStr != null){
            long id = Long.parseLong(idStr);
            memberService.deleteById(id);
        }
        else{ throw new RuntimeException("no-id existence"); }

        return "redirect:/member/manage/setting";
    }
}
