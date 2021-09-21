package com.emulate.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.BaseErrorEnum;
import com.emulate.core.result.ResultBody;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 继承spring过滤器
 */
public class BaseFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(request,response);
    }

    protected void writeError(HttpServletResponse response, BaseErrorEnum errorEnum) throws IOException {
        response.getWriter().write( JSONObject.toJSONString(ResultBody.error(errorEnum)));
    }
}
