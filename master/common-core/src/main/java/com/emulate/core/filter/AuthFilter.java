package com.emulate.core.filter;



import com.emulate.core.user.LoginUserDTO;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 客户端接口过登录接口滤器
 */
public class AuthFilter extends BaseFilter {

    /**
     * 存储会员用户信息方便业务层使用
     */
    public static final ThreadLocal<LoginUserDTO> clientUser = new ThreadLocal();
    /**
     * 存储后台用户信息方便业务层使用
     */
    public static final ThreadLocal<LoginUserDTO> backendUser = new ThreadLocal();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        if(接口不需要登陆校验){
//            chain.doFilter(request,response);
//            return;
//        }
//
//        if(校验TOKEN为空){
//           this.writeError();
//            return;
//        }
//
//        if(解析TOKEN错误){
//            this.writeError();
//            return;
//        }
//
//      存储解析出来的用户信息
        chain.doFilter(request,response);
    }


    /**
     * 获取存储的登录用户
     * @return
     */
    public static LoginUserDTO backendLoginUserDTO(){
        return backendUser.get();
    }

    /**
     * 获取存储的登录用户
     * @return
     */
    public static LoginUserDTO clientLoginUserDTO(){
        return clientUser.get();
    }
}
