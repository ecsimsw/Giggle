package com.giggle.Validator;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Form.JoinForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

//https://docs.spring.io/spring/docs/3.0.0.M4/reference/html/ch05s02.html

/*
^[0-9]*$	숫자
^[a-zA-Z]*$	영문자
^[가-힣]*$	한글
\\w+@\\w+\\.\\w+(\\.\\w+)?	E-Mail
^\d{2,3}-\d{3,4}-\d{4}$	전화번호
^01(?:0|1|[6-9])-(?:\d{3}|\d{4})-\d{4}$	휴대전화번호
\d{6} \- [1-4]\d{6}	주민등록번호
^\d{3}-\d{2}$	우편번호
 */

@Component
public class JoinValidator implements Validator {

    private static String[] disAllowed = new String[]{
            "master", "administer", "kimjinhwan"
    };

    @Override
    public boolean supports(Class<?> clazz) {
        return JoinForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinForm joinForm = (JoinForm)target;

        if(checkDisAllowedId(joinForm.getLoginId()))
            errors.rejectValue("loginId", "Disallowed login id");


    }

    private boolean checkDisAllowedId(String loginId){
        for(String id : disAllowed){
            if(loginId.equals(id)){ return true; }
        }
        return false;
    }

    private boolean checkLoginId(String loginId){

        Pattern pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]");

        if(loginId.matches(pattern)) {
            System.out.println("matches 숫자 포함");
        }else {
            System.out.println("matches 숫자 미포함");
        }
    }

}
