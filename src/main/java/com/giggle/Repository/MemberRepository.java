package com.giggle.Repository;

import com.giggle.Entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findByLoginId(String loginId){
        List<Member> selectedMembers = em.createQuery("select m from Member m where m.loginId = :loginId",Member.class)
            .setParameter("loginId",loginId)
            .getResultList();

        if(selectedMembers.isEmpty()) return null;

        else {
            return selectedMembers.get(0);
        }

        // loginId는 중복x
    }
}
