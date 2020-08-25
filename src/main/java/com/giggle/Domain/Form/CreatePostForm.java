package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePostForm {
    private String categoryId;
    private String title;
    private String content;
}
