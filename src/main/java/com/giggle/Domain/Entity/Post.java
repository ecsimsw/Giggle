package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    private String category;

    private String writer;

    private String title;

    private String content;

    @CreatedDate
    private LocalDateTime dateTime;

    private int viewCnt;
}
