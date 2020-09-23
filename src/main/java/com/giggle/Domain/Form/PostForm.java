package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostForm {
    private String categoryId;
    private String writer;
    private String title;
    private String content;
}
