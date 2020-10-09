package com.giggle.Validation;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.MemberType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    MemberType authority() default MemberType.member;
}
