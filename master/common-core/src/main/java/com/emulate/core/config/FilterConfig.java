package com.emulate.core.config;

import com.emulate.core.filter.AuthFilter;
import com.emulate.core.filter.SignFilter;
import com.emulate.core.yml.AuthSignYml;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.DispatcherType;


@Configuration
@ConditionalOnBean(value = {AuthSignYml.class})
@Import(value = AuthSignYml.class)
public class FilterConfig {


    @Bean
    public FilterRegistrationBean<AuthFilter> authFile(RedisTemplate redisTemplate,AuthSignYml authSignYml) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new AuthFilter(redisTemplate,authSignYml));
        registration.addUrlPatterns("/api/*");
        registration.setName("ClientAuthFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SignFilter> SignFilter(RedisTemplate redisTemplate,AuthSignYml authSignYml) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new SignFilter(redisTemplate,authSignYml));
        registration.addUrlPatterns("/api/*");
        registration.setName("SignFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE-1);
        return registration;
    }

}