package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    private int postCnt;
    private String description;
}
