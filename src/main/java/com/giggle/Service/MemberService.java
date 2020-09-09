package com.giggle.Service;

import com.giggle.Domain.Entity.MemberType;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Message.EjoinMessage;
import com.giggle.Message.EloginMessage;
import com.giggle.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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

        if(memberRepository.findByNickName(joinForm.getNickName()) != null){
            return EjoinMessage.nickNameDuplicate;
        }

        Member member = new Member();
        member.setLoginId(joinForm.getLoginId());
        member.setLoginPw(joinForm.getLoginPw());
        member.setName(joinForm.getName());
        member.setNickName(joinForm.getNickName());
        member.setMemberType(MemberType.PUBLIC);
        memberRepository.save(member);

        return EjoinMessage.success;
    }

    public Member getByLoginId(String loginId){
        Member member = memberRepository.findByLoginId(loginId);
        return member;
    }

    public Member updateMemberInfo(Long id, MemberInfo memberInfo){
        Member member = memberRepository.findById(id);
        memberRepository.setNickName(member, memberInfo.getNickName());
        return member;
    }
}
