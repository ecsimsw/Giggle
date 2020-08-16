package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePostForm {

    private String category;
    private String title;
    private String content;
}
