package com.giggle.Service;

import com.giggle.Domain.Entity.MemberType;
import com.giggle.Domain.Form.JoinForm;
import com.giggle.Domain.Form.LoginForm;
import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.MemberInfo;
import com.giggle.Validator.Message.EjoinMessage;
import com.giggle.Validator.Message.EloginMessage;
import com.giggle.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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

        if(memberRepository.findByName(joinForm.getName()) != null){
            return EjoinMessage.nickNameDuplicate;
        }

        Member member = new Member();
        member.setLoginId(joinForm.getLoginId());
        member.setLoginPw(joinForm.getLoginPw());
        member.setName(joinForm.getName());
        member.setMemberType(MemberType.member);
        memberRepository.save(member);

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
        memberRepository.delete(member);
    }
}
