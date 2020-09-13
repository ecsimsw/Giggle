package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class MainBoardImg implements Comparable<MainBoardImg>{
    @GeneratedValue
    @Id
    private Long id;

    @GeneratedValue
    private int order;

    private String fileName; //  "1test.png"

    @Override
    public int compareTo(MainBoardImg mainBoardImg){
        if(this.order < mainBoardImg.order){ return -1; }
        else{ return 1; }
    }
}
