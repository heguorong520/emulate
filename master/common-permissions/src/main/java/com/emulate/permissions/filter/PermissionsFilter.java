package com.emulate.permissions.filter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.emulate.permissions.user.UserDetail;
import com.emulate.permissions.util.PermissionsUserUtil;
import com.emulate.redis.service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//处理对应的角色权限-以及解析user信息存储到local线程gateway传过来的用户信息
public class PermissionsFilter extends OncePerRequestFilter {

    private final String USER_PERMS_CACHE_KEY = "EMULATE:BACKEND:SHIRO:PERMS:USER:";

    private RedisService redisService;

    private List<String> perms;

    public PermissionsFilter(RedisService redisService, List<String> perms) {
        this.redisService = redisService;
        this.perms = perms;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath().replace("/api/", "");
        path = path.replace("/", ":");
        //获取gateway向下游传递的用户信息--gateway验证token
        String userJson = request.getHeader("user");
        if(ObjectUtil.isEmpty(userJson)){
            chain.doFilter(request,response);
            return;
        }
        //解析并设置用户信息
        UserDetail userDetail = JSONObject.parseObject(userJson, UserDetail.class);
        PermissionsUserUtil.setUserInfo(userDetail);
        //判断是否为权限拦截路径
        if(!perms.contains(path)){
            chain.doFilter(request,response);
            return;
        }
        //判断用户是否为超管
        if(userDetail.getUserId().equals(1L)){
            chain.doFilter(request,response);
            return;
        }
        //判断用户是否拥有对应的权限
        List<String> userPermsList = (List<String>) redisService.get(USER_PERMS_CACHE_KEY + userDetail.getUserId());
        if (userPermsList != null && !userPermsList.contains(path)) {
            this.writeError(response);
            return;
        }
        chain.doFilter(request,response);
    }

    protected void writeError(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> error = new HashMap<>();
        error.put("code",HttpStatus.FORBIDDEN.value());
        error.put("msg","用户无权访问资源");
        response.getWriter().write(JSONObject.toJSONString(error));
    }

}
