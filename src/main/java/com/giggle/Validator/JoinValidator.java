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


        if(validLoginId(joinForm.getLoginId(), joinForm.getLoginPw()) == false){
            errors.rejectValue("loginId", "Disallowed joinForm");
        }
    }

    private boolean validLoginId(String loginId, String loginPw){

        for(String id : disAllowed){ if(loginId.equals(id)) return false; }

        Pattern pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]");

        if(pattern.matcher(loginId).find() == true){ return false; }

        if(loginId.length()<5 || loginId.length()>12){ return false; }

        pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]");

        if(pattern.matcher(loginPw).find() == false){ return false; }

        if(loginPw.length()<5 || loginPw.length()>12){ return false; }

        return true;
    }
}
