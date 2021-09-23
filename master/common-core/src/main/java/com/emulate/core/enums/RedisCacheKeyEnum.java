package com.emulate.core.enums;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public enum RedisCacheKeyEnum {
    BACKEND_TOKEN_KEY(7*24*60*60L,"EMULATE:BACKEND:TOKEN:USER:"),
    CAPTCHA_KEY(5*60L,"EMULATE:BACKEND:CAPTCHA:DEVICEID:");
    RedisCacheKeyEnum(Long time, String key){
        this.time = time;
        this.key = key;
    }
    private Long time;

    private String key;

    public Long getCacheTime() {
        return time;
    }

    public String getCacheKey() {
        return key;
    }



}
