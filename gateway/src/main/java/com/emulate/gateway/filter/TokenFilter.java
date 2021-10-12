package com.emulate.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.yml.AuthSignYml;
import com.emulate.gateway.util.FilterCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



@Slf4j
@Component
@AllArgsConstructor
public class TokenFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthSignYml authSignYml;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (FilterCommonUtil.isFinish(exchange)) {
            return chain.filter(exchange);
        }
        String path = FilterCommonUtil.getPath(exchange);
        String token = exchange.getRequest().getHeaders().getFirst(HeaderKeyEnum.AUTHORIZATION.getName());
        String clientType = exchange.getRequest().getHeaders().getFirst(HeaderKeyEnum.CLIENT_TYPE.getName());
        if (ObjectUtil.isNotEmpty(token)) {
            String userJson;
            if ("backend".equals(clientType)) {
                userJson = TokenUtil.verifyTokenBackend(token);
            } else {
                userJson = TokenUtil.verifyTokenClient(token);
            }
            if (userJson == null) {
                return FilterCommonUtil.webFluxResponseWriter(exchange.getResponse(), GlobalErrorEnum.无权访问);
            }
            ServerHttpRequest newRequest = exchange.getRequest().mutate().header("user", userJson).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        if (!authSignYml.getEnableAuth() || FilterCommonUtil.verifyPath(path, authSignYml.getNoAuthList())) {
            return chain.filter(exchange);
        }
        return FilterCommonUtil.webFluxResponseWriter(exchange.getResponse(), GlobalErrorEnum.无权访问);
    }

    @Override
    public int getOrder() {
        return -200;
    }


}
