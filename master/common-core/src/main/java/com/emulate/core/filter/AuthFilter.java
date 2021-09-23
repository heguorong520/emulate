package com.emulate.core.filter;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.user.LoginUserDTO;
import com.emulate.core.yml.AuthSignYml;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 客户端接口过登录接口滤器
 */
public class AuthFilter extends BaseFilter {

    private RedisTemplate redisTemplate;

    private AuthSignYml authSignYml;

    public AuthFilter(RedisTemplate redisTemplate, AuthSignYml authSignYml) {
        this.redisTemplate = redisTemplate;
        this.authSignYml = authSignYml;
    }


    public static final ThreadLocal<LoginUserDTO> clientUser = new ThreadLocal();
    public static final ThreadLocal<LoginUserDTO> backendUser = new ThreadLocal();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String token = request.getHeader(HeaderKeyEnum.AUTHORIZATION.getName());
            String clientType = request.getHeader(HeaderKeyEnum.CLIENT_TYPE.getName());
            if (ObjectUtil.isNotEmpty(token)) {
                String userJson;
                if ("backend".equals(clientType)) {
                    userJson = TokenUtil.verifyTokenBackend(token);
                } else {
                    userJson = TokenUtil.verifyTokenClient(token);
                }
                if (userJson == null) {
                    this.writeError(response, GlobalErrorEnum.TOKEN异常);
                }
                if ("backend".equals(clientType)) {
                    backendUser.set(BeanUtil.toBean(userJson, LoginUserDTO.class));
                } else {
                    clientUser.set(BeanUtil.toBean(userJson, LoginUserDTO.class));
                }
                chain.doFilter(request, response);
                return;
            }
            if (!authSignYml.getEnableAuth() || verifyPath(request.getServletPath(), authSignYml.getNoAuthList())) {
                chain.doFilter(request, response);
                return;
            }
            this.writeError(response, GlobalErrorEnum.TOKEN异常);
        }catch (Exception e){
            return;
        }
    }


    /**
     * 获取存储的登录用户
     *
     * @return
     */
    public static LoginUserDTO backendLoginUserDTO() {
        return backendUser.get();
    }

    /**
     * 获取存储的登录用户
     *
     * @return
     */
    public static LoginUserDTO clientLoginUserDTO() {
        return clientUser.get();
    }

}
