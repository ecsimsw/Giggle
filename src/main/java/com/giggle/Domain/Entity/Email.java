package com.giggle.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Email {
    @Id
    @GeneratedValue
    private Long id;

    private String address;

    private String key;

    private boolean used = false;
}
