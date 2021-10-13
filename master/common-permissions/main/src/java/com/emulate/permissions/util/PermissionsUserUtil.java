package com.emulate.permissions.util;

import com.emulate.permissions.user.UserDetail;

//获取用户信息
public class PermissionsUserUtil {
    private static final ThreadLocal<UserDetail> local = new ThreadLocal<>();

    public static void setUserInfo(UserDetail userDetail){
        local.set(userDetail);
    }

    public static String getUsername(){
        UserDetail userDetail = local.get();
        if(userDetail !=null){
            return userDetail.getUsername();
        }
        return null;
    }

    public static Long getUserId(){
        UserDetail userDetail = local.get();
        if(userDetail !=null){
            return userDetail.getUserId();
        }
        return null;
    }

    public static UserDetail getUserDetail(){
        return local.get();
    }

    public static void remove(){
       if(local.get() != null){
           local.remove();
       }
    }

}
