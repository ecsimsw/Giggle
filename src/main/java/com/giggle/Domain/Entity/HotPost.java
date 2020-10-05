package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class HotPost {

    @Id
    @GeneratedValue
    @Column(name="hot_post_id")
    private Long id;

    @OneToOne
    private Post post;
}
