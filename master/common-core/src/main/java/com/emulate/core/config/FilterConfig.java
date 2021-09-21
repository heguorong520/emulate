package com.emulate.core.config;

import com.emulate.core.filter.AuthFilter;
import com.emulate.core.filter.SignFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<AuthFilter> authFile() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("/*/api/*");
        registration.setName("ClientAuthFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE-1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SignFilter> SignFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new SignFilter());
        registration.addUrlPatterns("/*");
        registration.setName("SignFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}