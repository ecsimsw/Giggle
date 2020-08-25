package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class MainCategory {
    @Id
    @GeneratedValue
    @Column(name="main_category_id")
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL)
    private List<MiddleCategory> middleCategoryList = new ArrayList<>();

    private int postCnt;
}
