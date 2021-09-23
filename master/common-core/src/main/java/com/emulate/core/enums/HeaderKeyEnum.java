package com.emulate.core.enums;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public enum HeaderKeyEnum {

    DEVICEID("deviceId","deviceId不能为空"),
    RANDOM("random","random不能为空"),
    TIME("time","time不能为空"),
    VERSION("version","version不能为空"),
    CLIENT_TYPE("clientType","clientType不能为空"),
    AUTHORIZATION("AUTHORIZATION","Authorization"),
    SIGN("sign","sign不能为空");
    HeaderKeyEnum(String key, String error){
        this.error = error;
        this.key = key;
    }
    private String key;

    private String error;

    public String getName() {
        return key;
    }

    public String getMsg() {
        return error;
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        return attrs.getRequest();
    }
    //获取请求头对应的值
    public  String value() {
        HttpServletRequest request = getCurrentRequest();
        if(getCurrentRequest() == null){
            return null;
        }
        return request.getHeader(this.getName());
    }
}
