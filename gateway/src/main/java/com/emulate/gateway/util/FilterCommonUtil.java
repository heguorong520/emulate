package com.emulate.gateway.util;

import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.result.ResultBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class FilterCommonUtil {

    public static final String URI = "/v2/api-docs";

    public static final String JSON_UTF8 = "application/json;charset=UTF-8";

    public static final String BODY_NAME = "cachedRequestBodyObject";


    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, GlobalErrorEnum globalErrorEnum) {
        response.setStatusCode(HttpStatus.valueOf(globalErrorEnum.getCode()));
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_UTF8);
        ResultBody<?> result = ResultBody.error(globalErrorEnum);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 校验请求地址是否存在
     *
     * @param url
     * @return
     */
    public static boolean verifyPath(String url, List<String> urlList) {
        Boolean result = false;
        AntPathMatcher matcher = new AntPathMatcher();
        for (String path : urlList) {
            if (matcher.match(path, url)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 判断是否结束过滤器执行
     * @param exchange
     * @return
     */
    public static boolean isFinish(ServerWebExchange exchange){
        boolean result = false;
        String path = exchange.getRequest().getURI().getPath();
        if (StringUtils.endsWithIgnoreCase(path, FilterCommonUtil.URI)) {
            result= true;
        }
        //feign直接返回
        if (path.contains("/rpc")) {
            result= true;
        }
        return result;
    }

    /**
     * 截取接口路径 所有服务的路径/模块名/api/开头
     * @param exchange
     * @return
     */
    public static String getPath(ServerWebExchange exchange){
        String path = exchange.getRequest().getURI().getPath();
        if(path.contains("/api")){
            String module = path.substring(0,path.indexOf("/api"));
            path = path.replace(module,"");
        }
        return path;
    }
}
