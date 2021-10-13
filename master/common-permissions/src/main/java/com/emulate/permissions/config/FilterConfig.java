package com.emulate.permissions.config;


import cn.hutool.core.util.ObjectUtil;
import com.emulate.permissions.annotation.Permissions;
import com.emulate.permissions.filter.PermissionsFilter;
import com.emulate.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.DispatcherType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@ComponentScan(value = "com.emulate.permissions")
@ConditionalOnBean(value = {RedisService.class})
public class FilterConfig {

    @SuppressWarnings("all")
    @Bean
    public FilterRegistrationBean<PermissionsFilter> PermissionsFilter(RedisService redisService, ApplicationContext context) {
        //通过上下文获取设置权限的接口
        List<String> perms = new ArrayList<>();
        try{
            Map<String, Object> openClz = context.getBeansWithAnnotation(RequestMapping.class);
            for (Object clzObj : openClz.values()) {
                Class clz = Class.forName(clzObj.getClass().getName(), true, Permissions.class.getClassLoader());
                for (Method method : clz.getDeclaredMethods()) {
                    Permissions shiroPermsValidate = method.getAnnotation(Permissions.class);
                    if (shiroPermsValidate != null && ObjectUtil.isNotEmpty(shiroPermsValidate.perms())) {
                        perms.add(shiroPermsValidate.perms());
                    }
                }
            }
        }catch (Exception e) {
            log.info("获取角色接口失败{}",e);
        }
        log.info("perms:{}",perms);
        FilterRegistrationBean<PermissionsFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new PermissionsFilter(redisService,perms));
        registration.addUrlPatterns("/api/*");
        registration.setName("PermissionsFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

}
