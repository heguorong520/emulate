/*
package com.emulate.core.filter;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.enums.RedisCacheKeyEnum;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.user.LoginUserDTO;
import com.emulate.core.yml.AuthSignYml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

*/
/**
 * 权限过滤
 *//*

@Slf4j
public class AuthFilter extends BaseFilter {

    private RedisTemplate redisTemplate;

    private AuthSignYml authSignYml;

    private List<String> pathPerms;

    public AuthFilter(RedisTemplate<Object,Object> redisTemplate, AuthSignYml authSignYml, List<String> pathPerms) {
        this.redisTemplate = redisTemplate;
        this.authSignYml = authSignYml;
        this.pathPerms = pathPerms;
    }

    public static final ThreadLocal<LoginUserDTO> clientUser = new ThreadLocal();
    public static final ThreadLocal<LoginUserDTO> backendUser = new ThreadLocal();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String path = request.getServletPath().replace("/api", "");
            path = path.replace("/", ":");
            List<String> userPermsList = (List<String>) redisTemplate.opsForValue().get(RedisCacheKeyEnum.USER_SHIRO_PERMS_KEY.getCacheKey());
            if (pathPerms.contains(path) && (userPermsList != null && !userPermsList.contains(path))) {
                this.writeError(response, GlobalErrorEnum.无权访问);
                return;
            }
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
                    this.writeError(response, GlobalErrorEnum.无权访问);
                    return;
                }
                if ("backend".equals(clientType)) {
                    JSONObject jsonObject = JSONObject.parseObject(userJson);
                    LoginUserDTO userDTO = BeanUtil.toBean(jsonObject, LoginUserDTO.class);
                    backendUser.set(userDTO);
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(userJson);
                    LoginUserDTO userDTO = BeanUtil.toBean(jsonObject, LoginUserDTO.class);
                    clientUser.set(userDTO);
                }

                chain.doFilter(request, response);
                return;
            }
            if (!authSignYml.getEnableAuth() || verifyPath(request.getServletPath(), authSignYml.getNoAuthList())) {
                chain.doFilter(request, response);
                return;
            }
            this.writeError(response, GlobalErrorEnum.无权访问);
        } catch (Exception e) {
            log.info("权限控制处理异常:{}", e);
            return;
        }
    }


    */
/**
     * 获取存储的登录用户
     *
     * @return
     *//*

    public static LoginUserDTO backendLoginUserDTO() {
        return backendUser.get();
    }

    */
/**
     * 获取存储的登录用户
     *
     * @return
     *//*

    public static LoginUserDTO clientLoginUserDTO() {
        return clientUser.get();
    }

}
*/
