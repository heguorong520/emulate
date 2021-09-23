package com.emulate.core.controller;

import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.filter.AuthFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api")
public abstract class BaseController {

    protected Long getBackendUserId(){
        return AuthFilter.backendLoginUserDTO().getUserId();
    }
    protected String getBackendUserName(){
        return AuthFilter.backendLoginUserDTO().getUsername();
    }

    protected Long getUserId(){
        return AuthFilter.clientLoginUserDTO().getUserId();
    }
    protected String getUserName(){
        return AuthFilter.clientLoginUserDTO().getUsername();
    }
}
