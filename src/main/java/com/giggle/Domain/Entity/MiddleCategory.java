package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class MiddleCategory {

    @Id
    @GeneratedValue
    @Column(name = "middle_category_id")
    private Long id;

    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="main_category_id")
    private MainCategory mainCategory;

    @OneToMany(mappedBy = "middleCategory", cascade = CascadeType.ALL)
    private List<Category> categoryList = new ArrayList<>();

    private Integer postCnt;
}
