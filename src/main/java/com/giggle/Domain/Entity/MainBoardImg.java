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

    private int orderImg;

    // expected "identifier"; SQL statement : 사용할 수 없는 테이블 명 일 경우 (order)

    private String fileName; //  "1test.png"

    @Override
    public int compareTo(MainBoardImg mainBoardImg){
        if(this.orderImg < mainBoardImg.orderImg){ return -1; }
        else{ return 1; }
    }
}
