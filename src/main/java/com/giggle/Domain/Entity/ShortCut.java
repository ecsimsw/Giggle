package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class ShortCut {

    @GeneratedValue
    @Id
    private Long id;

    private String title;
    private Long categoryId;
    private String description;
    private String color;
}
