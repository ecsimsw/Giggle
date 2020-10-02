package com.giggle.Repository;

import com.giggle.Domain.Entity.Email;
import com.giggle.Domain.Entity.MainCategory;
import com.giggle.Domain.Entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member){
        if(member.getId()==null){
            em.persist(member); }
        else{
            em.merge(member);
        }
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
    public Member findByName(String name){
        List<Member> selectedMembers = em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name",name)
                .getResultList();
        if(selectedMembers.isEmpty()) return null;
        else { return selectedMembers.get(0); }
    }

    public Member findByEmail(String email){
        List<Member> selectedMembers = em.createQuery("select m from Member m where m.email = :email",Member.class)
                .setParameter("email",email)
                .getResultList();
        if(selectedMembers.isEmpty()) return null;
        else { return selectedMembers.get(0); }
    }

    public void delete(Member member){
        em.remove(member);
    }

    public void delete(Email email){
        em.remove(email);
    }

    public List<Member> findAllMember(){
        List<Member> members= em.createQuery("select m from Member m", Member.class)
                .getResultList();
        return members;
    }

    public void saveEmail(Email email){
        if(email.getId()==null){
            em.persist(email); }
        else{
            em.merge(email);
        }
    }

    public Email findEmailByAddress(String address){
        List<Email> selectedEmail = em.createQuery("select e from Email e where e.address = :address",Email.class)
                .setParameter("address",address)
                .getResultList();
        if(selectedEmail.isEmpty()) return null;
        else { return selectedEmail.get(0); }
    }

    public Email findEmailById(long id){
        return em.find(Email.class, id);
    }

    public List findAllProfile(){
        List<Member> members= em.createQuery("select m from Member m", Member.class)
                .getResultList();

        List<String> stringList = new ArrayList<>();
        for(Member m : members){
            stringList.add(m.getProfileImg());
        }

        return stringList;
    }
}
