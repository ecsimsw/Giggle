package com.giggle.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    private String writer;

    private String title;

    private String content;

    private LocalDateTime dateTime;

    private int viewCnt;

}
