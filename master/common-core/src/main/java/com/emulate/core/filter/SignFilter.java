package com.emulate.core.filter;



import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 签名验证
 */
public class SignFilter extends BaseFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
       /* if(没有开启加签 || 接口地址不需要加签加签){
            chain.doFilter(request,response);
            return;
        }

        if(校验参数是否为空){
            this.writeError();
            return;
        }

        服务端加签密文 = 加签方法(私钥+参数生产);

        if(服务端加签密文 != 客户端加签密文){
            this.writeError();
            return;
        }

        if(请求ID在5分钟内使用过){
            this.writeError();
            return;
        }

        if(时间搓大于当前时间1分钟 || 时间搓小于当前时间1分钟 ){
            this.writeError();
            return;
        }
        post取参数需要做特殊处理,可以限定客户端只使用POST请求,统一处理*/
        chain.doFilter(request,response);
    }


}
