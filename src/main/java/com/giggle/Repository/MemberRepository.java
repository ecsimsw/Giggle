package com.giggle.Repository;

import com.giggle.Domain.Entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findById(Long id){
       return em.find(Member.class, id);
    }
    // loginId 는 중복x
    public Member findByLoginId(String loginId){
        List<Member> selectedMembers = em.createQuery("select m from Member m where m.loginId = :loginId",Member.class)
            .setParameter("loginId",loginId)
            .getResultList();
        if(selectedMembers.isEmpty()) return null;
        else { return selectedMembers.get(0); }
    }

    // nickName 는 중복x
    public Member findByNickName(String nickName){
        List<Member> selectedMembers = em.createQuery("select m from Member m where m.nickName = :nickName",Member.class)
                .setParameter("nickName",nickName)
                .getResultList();
        if(selectedMembers.isEmpty()) return null;
        else { return selectedMembers.get(0); }
    }

    public void setNickName(Member member, String nickName){
        member.setNickName(nickName);
        em.flush();
    }
}
