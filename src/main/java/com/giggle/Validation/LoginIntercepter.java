package com.giggle.Validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LoginIntercepter implements HandlerInterceptor {

    public List loginEssential
            = Arrays.asList("/post/**", "/comment/**",  "/category/**", "/member/manage/**", "/main/edit/**");

    public List loginInessential
            = Arrays.asList("/post/board/**", "/post/read/**", "/post/like/**" );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginId = (String)request.getSession().getAttribute("loginId");

        if(loginId != null){
            return true;
        }
        else{
            String destUri = request.getRequestURI();
            String destQuery = request.getQueryString();
            String dest = (destQuery == null) ? destUri : destUri+"?"+destQuery;
            request.getSession().setAttribute("dest", dest);
            response.sendRedirect("/member/login");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

}
