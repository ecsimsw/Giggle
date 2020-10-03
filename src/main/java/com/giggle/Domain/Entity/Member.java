package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<Like> like = new HashSet<>();
}
