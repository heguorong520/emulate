package com.emulate.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@SpringBootApplication
public class GetawayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GetawayApplication.class,args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                //拦截请求类型为POST Content-Type application/json application/json;charset=UTF-8
                .route(r -> r
                        .header(HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE)
                        .and()
                        .method(HttpMethod.POST)
                        .and()
                        //获取缓存中的请求体
                        .readBody(String.class, readBody -> true)
                        .and()
                        .path("lb:/404")
                        .uri("lb:/404")).build();
    }
}
