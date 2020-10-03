package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "POST_LIKE")
public class Like {

    @Id
    @GeneratedValue
    @Column(name="like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Like){
            if(((Like) obj).id == (this.id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.id.hashCode();
    }
}
