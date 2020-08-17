package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int postCnt;
    private String description;
}
