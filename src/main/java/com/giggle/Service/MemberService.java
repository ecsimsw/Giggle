package com.giggle.Service;

import com.giggle.Entity.LoginForm;
import com.giggle.Entity.Member;
import com.giggle.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean loginCheck(LoginForm loginForm){
        String attemptedId = loginForm.getLoginId();
        String attemptedPw = loginForm.getLoginPw();

        Member selected = memberRepository.findByLoginId(attemptedId);

        if(selected == null){ return false; }

        if(selected.getLoginPw().equals(attemptedPw)){
            return true;
        }

        return false;
    }

    @Transactional
    public Long join(Member member){
        memberRepository.save(member);
        return member.getId();
    }
}
