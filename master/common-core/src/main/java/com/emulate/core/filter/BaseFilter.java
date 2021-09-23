package com.emulate.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.result.ResultBody;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 继承spring过滤器
 */
public class BaseFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(request,response);
    }

    protected void writeError(HttpServletResponse response, GlobalErrorEnum errorEnum) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write( JSONObject.toJSONString(ResultBody.error(errorEnum)));
        throw new CustomizeException(errorEnum);
    }

    /**
     * 校验请求地址是否存在
     * @param url
     * @return
     */
    protected boolean verifyPath(String url, List<String> urlList){
        Boolean result = false;
        AntPathMatcher matcher= new AntPathMatcher();
        for (String path :urlList){
            if(matcher.match(path,url)){
                result = true;
                break;
            }
        }
        return result;
    }
}
