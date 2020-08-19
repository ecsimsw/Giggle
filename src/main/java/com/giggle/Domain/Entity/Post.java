package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Post{
    @Id
    @GeneratedValue
    private Long id;

    private String category;

    private String writer;

    private String title;

    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    private String content;

    @CreatedDate
    private LocalDateTime dateTime;

    private int viewCnt;
}
