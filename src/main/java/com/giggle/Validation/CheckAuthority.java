package com.giggle.Validation;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class CheckAuthority {

    public String checkLogin(HttpSession httpSession){
        String loginId = (String) httpSession.getAttribute("loginId");
        if(loginId == null){
            throw new RuntimeException("Wrong access _ Login session information does not exist");
        }
        return loginId;
    }

    public String checkAuthority(HttpSession httpSession){
        String authority = (String) httpSession.getAttribute("authority");
        if(authority == null){
            throw new RuntimeException("Wrong access _ Authority session information does not exist");
        }
        return authority;
    }

    public boolean checkOwner(HttpSession httpSession, String ownerId){
        String loginId = checkLogin(httpSession);
        if(!loginId.equals(ownerId)){
            return false;
        }
        return true;
    }


    public boolean checkAdmin(HttpSession httpSession){
        String authority = checkAuthority(httpSession);
        if(authority.equals("admin") || authority.equals("master")){
            return true;
        }
        return false;
    }

    public boolean checkMaster(HttpSession httpSession){
        String authority = checkAuthority(httpSession);
        if(authority.equals("master")){
            return true;
        }
        return false;
    }

}
