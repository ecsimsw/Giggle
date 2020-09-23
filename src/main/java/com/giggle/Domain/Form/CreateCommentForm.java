package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateCommentForm {
    private String writer;
    private String content;
    private String postId;
    private String commentId;
}
