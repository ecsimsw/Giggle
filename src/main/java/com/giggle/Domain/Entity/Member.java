package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String loginPw;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;
}
