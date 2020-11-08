package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter
public class DashBoard {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private DashBoardType type;

    private String width;  // narrow, wide

    private String height; // short, long

    private String title;   // latestPost, freePost, linkPost

    private String content;  // freePost

    private Long linkId; // latestPost - categoryId
                         // linkPost - postId

    private Integer spotType;  // 0 : Don't care
                           // 1 : Small blocks to go up
                           // 2 : Small blocks to go down
}
