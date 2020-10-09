package com.giggle.Validation;

import com.giggle.Domain.Entity.Member;
import com.giggle.Domain.Entity.MemberType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Override

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(handler instanceof HandlerMethod == false){
            log.info(handler.toString());
            return false;
        }

        HandlerMethod method = (HandlerMethod)handler;
        Permission permission = method.getMethodAnnotation(Permission.class);

        if(permission == null) {
            return true;
        }

        String authority = (String)request.getSession().getAttribute("authority");

        if(permission.authority().equals(MemberType.admin)){
            if(authority.equals(MemberType.admin.name()) || authority.equals(MemberType.master.name())){
                return true;
            }
        }
        else if(permission.authority().equals(MemberType.master)){
            if(authority.equals(MemberType.master.name())){
                return true;
            }
        }

        throw new RuntimeException("No access");
    }
}

