package com.giggle.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Post{
    @Id
    @GeneratedValue
    @Column(name="post_id")
    private Long id;

    private String writer;

    private String profileImg;

    private String title;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @Lob
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime dateTime;

    private int viewCnt;

    // Mapping LOB Data in Hibernate
    // https://www.baeldung.com/hibernate-lob

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> like = new HashSet<>();
}
