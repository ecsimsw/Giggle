package com.giggle.Domain.Form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateMiddleCategoryForm {
    private long mainCategoryId;
    private String name;
    private String description;
}
