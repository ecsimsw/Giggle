package com.giggle.Validator;

import com.giggle.Domain.Form.CreateCategoryForm;
import com.giggle.Domain.Form.JoinForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@Component
public class CategoryValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateCategoryForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateCategoryForm categoryForm = (CreateCategoryForm)target;

        if(validLoginId(categoryForm.getName())==false){
            errors.rejectValue("categoryName", "Disallowed categoryForm");
        }
    }

    private boolean validLoginId(String categoryName){
        Pattern pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]");

        if(pattern.matcher(categoryName).find() == true){ return false; }

        if(categoryName.length()<1 || categoryName.length()>10){ return false; }

        return true;
    }
}

