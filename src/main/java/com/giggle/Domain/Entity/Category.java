package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    private Long mainCatId;
    private Long middleCatId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="middle_category_id")
    private MiddleCategory middleCategory;

    private int postCnt;
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
}
