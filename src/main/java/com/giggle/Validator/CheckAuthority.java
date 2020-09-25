package com.giggle.Validator;

import javax.servlet.http.HttpSession;

public class CheckAuthority {

    public static boolean checkAuthority(HttpSession httpSession, String owner){
        String authority = (String) httpSession.getAttribute("authority");
        boolean isAdmin = authority.equals("admin") || authority.equals("master");
        boolean isOwner = httpSession.getAttribute("loginId").equals(owner);
        if(!isAdmin && !isOwner){ return false;}

        return true;
    }

}
