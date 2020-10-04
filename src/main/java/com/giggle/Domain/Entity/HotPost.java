package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class HotPost {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "hotPost")
    @JoinColumn(name="post_id")
    private Post post;
}
