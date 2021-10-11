package com.emulate.gateway.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.enums.RedisCacheKeyEnum;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.result.ResultBody;
import com.emulate.core.user.LoginUserDTO;
import com.emulate.core.yml.AuthSignYml;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import java.util.List;


@Slf4j
@Component
@AllArgsConstructor
public class TokenFilter implements GlobalFilter, Ordered {
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private AuthSignYml authSignYml;

	private static final String URI = "/v2/api-docs";

	public static final String JSON_UTF8 = "application/json;charset=UTF-8";


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info(exchange.getRequest().getPath().contextPath().value());
		String path = exchange.getRequest().getURI().getPath();
		if (StringUtils.endsWithIgnoreCase(path,URI )) {
			return chain.filter(exchange);
		}
		if(path.contains("/rpc")){
			return chain.filter(exchange);
		}
		if(path.contains("/api")){
			String module = path.substring(0,path.indexOf("/api"));
			path = path.replace("/api","").replace(module,"");
		}
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
				return webFluxResponseWriter(exchange.getResponse(),GlobalErrorEnum.无权访问);
			}
			ServerHttpRequest newRequest = exchange.getRequest().mutate().header("user",userJson).build();
			return chain.filter(exchange.mutate().request(newRequest).build());
		}
		if (!authSignYml.getEnableAuth() || verifyPath(path, authSignYml.getNoAuthList())) {
			return chain.filter(exchange);
		}
		return webFluxResponseWriter(exchange.getResponse(),GlobalErrorEnum.无权访问);
	}

	@Override
	public int getOrder() {
			return -200;
	}


	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response,GlobalErrorEnum globalErrorEnum) {
		response.setStatusCode(HttpStatus.valueOf(globalErrorEnum.getCode()));
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_UTF8);
		ResultBody<?> result = ResultBody.error(globalErrorEnum);
		DataBuffer dataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(result).getBytes());
		return response.writeWith(Mono.just(dataBuffer));
	}

	/**
	 * 校验请求地址是否存在
	 * @param url
	 * @return
	 */
	protected boolean verifyPath(String url, List<String> urlList){
		Boolean result = false;
		AntPathMatcher matcher= new AntPathMatcher();
		for (String path :urlList){
			if(matcher.match(path,url)){
				result = true;
				break;
			}
		}
		return result;
	}
}
