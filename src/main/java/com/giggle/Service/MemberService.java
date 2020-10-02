package com.giggle.Service;

import com.giggle.Domain.Entity.Email;
import com.giggle.Domain.Entity.MemberType;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Validator.Message.EjoinMessage;
import com.giggle.Validator.Message.EloginMessage;
import com.giggle.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.desktop.AboutEvent;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    @Autowired private JavaMailSender javaMailSender;

    public Member getById(long id){
        return memberRepository.findById(id);
    }

    public EloginMessage loginCheck(LoginForm loginForm){
        String attemptedId = loginForm.getLoginId();
        String attemptedPw = loginForm.getLoginPw();
        Member selected = memberRepository.findByLoginId(attemptedId);
        
        if(selected == null){ return EloginMessage.nonExistLoginId; }
        if(!selected.getLoginPw().equals(attemptedPw)){ return EloginMessage.wrongLoginPw; }
        else if(selected.getLoginPw().equals(attemptedPw)){ return EloginMessage.success;}

        return null; // error
    }

    @Transactional
    public EjoinMessage join(JoinForm joinForm){
        if(memberRepository.findByLoginId(joinForm.getLoginId()) != null){
            return EjoinMessage.loginIdDuplicate;
        }

        if(memberRepository.findByName(joinForm.getName()) != null){
            return EjoinMessage.nickNameDuplicate;
        }

        Member member = new Member();
        member.setLoginId(joinForm.getLoginId());
        member.setLoginPw(joinForm.getLoginPw());
        member.setName(joinForm.getName());
        member.setMemberType(MemberType.member);
        member.setEmail(joinForm.getEmail());
        memberRepository.save(member);

        Email tempMail = memberRepository.findEmailByAddress(member.getEmail());
        this.changeEmailUsed(tempMail.getId());

        return EjoinMessage.success;
    }


    public Member getByLoginId(String loginId){
        Member member = memberRepository.findByLoginId(loginId);
        return member;
    }

    public Member getByName(String userName){
        Member member = memberRepository.findByName(userName);
        return member;
    }

    public List<Member> getAllMember(){
        List<Member> members = memberRepository.findAllMember();
        return members;
    }

    public Member getByEmail(String email){
        Member member = memberRepository.findByEmail(email);
        return member;
    }

    @Transactional
    @Async
    public void sendAuthMail(String to) {
        Email email = memberRepository.findEmailByAddress(to);
        String key = makeRandomKey();

        if(email != null){
            // 인증 요청만 하고, 실제 인증하지 않은 더미 이메일
            // 새로운 키 값 업데이트 -> 병합

            if(email.isUsed()==true){
                throw new RuntimeException("Mail already used");
            }

            email.setKey(key);
        }
        else{
            // 아예 새로 요청한 이메일 주소
            email = new Email();
            email.setKey(key);
            email.setAddress(to);
            email.setUsed(false);
        }
        memberRepository.saveEmail(email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("geeksecsimsw@gmail.com");
        message.setTo(to);
        message.setSubject("[From Giggle] 이메일 인증");
        message.setText("인증 번호는 "+key+" 입니다.");

        javaMailSender.send(message);
    }

    private static final String NUMBERS = "0123456789";
    private static final String ALPHABET_LOWER_CASE = "abcdefghijkmnlopqrstuvwxyz";
    private static final String ALPHABET_UPPER_CASE = "ABCDEFGHIJKMNLOPQRSTUVWXYZ";

    private String makeRandomKey(){
        Random rand = new Random();
        StringBuilder key = new StringBuilder();

        int r;
        for(int i=0; i<15; i++){
            r= rand.nextInt(3);

            switch (r){
                case 0:
                    key.append(ALPHABET_LOWER_CASE.charAt(rand.nextInt(26)));
                    break;
                case 1:
                    key.append(ALPHABET_UPPER_CASE.charAt(rand.nextInt(26)));
                    break;
                case 2:
                    key.append(NUMBERS.charAt(rand.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    public boolean verifyEmail(String address, String enteredKey){
        Email email = memberRepository.findEmailByAddress(address);
        String realKey = email.getKey();
        if(realKey.equals(enteredKey)){
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional
    public void changeEmailUsed(Long id){
        Email email = memberRepository.findEmailById(id);
        email.setUsed(true);
    }

    @Transactional
    public Member updateMemberInfo(Long id, MemberInfo memberInfo){
        Member member = memberRepository.findById(id);
        member.setName(memberInfo.getName());

        if(memberInfo.getLoginId()!=null){
            if(!memberInfo.getLoginId().equals("")){
                member.setLoginId(memberInfo.getLoginId());
            }
        }

        if(memberInfo.getType()!=null){
            if(!memberInfo.getType().equals("")){
                MemberType memberType = MemberType.valueOf(memberInfo.getType());
                member.setMemberType(memberType);
            }
        }

        return member;
    }

    @Transactional
    public void deleteById(Long id){
        Member member = memberRepository.findById(id);

        Email userEmail = memberRepository.findEmailByAddress(member.getEmail());

        memberRepository.delete(userEmail);

        memberRepository.delete(member);


    }
}
