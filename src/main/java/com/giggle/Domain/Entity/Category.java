package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

//    @Enumerated(EnumType.STRING)
//    private CommunityType communityType;

    private Long mainCatId;
    private Long middleCatId;

    @ManyToOne
    @JoinColumn(name="middle_category_id")
    private MiddleCategory middleCategory;

    private int postCnt;
    private String description;
}
