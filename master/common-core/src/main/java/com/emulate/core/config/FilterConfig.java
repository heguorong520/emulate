package com.emulate.core.config;

import cn.hutool.core.util.ObjectUtil;
import com.emulate.core.annotation.BackendShiroValidate;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.filter.SignFilter;
import com.emulate.core.yml.AuthSignYml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.DispatcherType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Configuration
@ConditionalOnBean(value = {AuthSignYml.class})
@Import(value = AuthSignYml.class)
public class FilterConfig {


    @Bean
    public FilterRegistrationBean<AuthFilter> authFile(RedisTemplate<Object,Object> redisTemplate, AuthSignYml authSignYml, ApplicationContext context) {
        //通过上下文获取设置权限的接口
        List<String> perms = new ArrayList<>();
        try{
            Map<String, Object> openClz = context.getBeansWithAnnotation(RequestMapping.class);
            for (Object clzObj : openClz.values()) {
                Class clz = Class.forName(clzObj.getClass().getName(), true, BackendShiroValidate.class.getClassLoader());
                for (Method method : clz.getDeclaredMethods()) {
                    BackendShiroValidate shiroPermsValidate = method.getAnnotation(BackendShiroValidate.class);
                    if (shiroPermsValidate != null && ObjectUtil.isNotEmpty(shiroPermsValidate.perms())) {
                        perms.add(shiroPermsValidate.perms());
                    }
                }
            }
        }catch (Exception e) {
            log.info("获取权限接口失败{}",e);
        }
        log.info("shrio-perms:{}",perms);
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new AuthFilter(redisTemplate, authSignYml,perms));
        registration.addUrlPatterns("/api/*");
        registration.setName("authFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SignFilter> SignFilter(RedisTemplate<Object,Object> redisTemplate, AuthSignYml authSignYml) {
        FilterRegistrationBean<SignFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new SignFilter(redisTemplate, authSignYml));
        registration.addUrlPatterns("/api/*");
        registration.setName("SignFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registration;
    }


}