package com.emulate.gateway.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.utils.AESUtil;
import com.emulate.core.yml.AuthSignYml;
import com.emulate.gateway.util.FilterCommonUtil;
import com.emulate.redis.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@AllArgsConstructor
public class SignFilter implements GlobalFilter, Ordered {
    @Autowired
    private RedisService redisService;

    @Autowired
    private AuthSignYml authSignYml;


    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (FilterCommonUtil.isFinish(exchange)) {
            return chain.filter(exchange);
        }
        String path = FilterCommonUtil.getPath(exchange);
        if (!authSignYml.getEnableSign() || FilterCommonUtil.verifyPath(path, authSignYml.getNoSignList())) {
            return chain.filter(exchange);
        }
        TreeMap<String, Object> paramMap = new TreeMap<>();
        //校验请求头参数
        for (HeaderKeyEnum headerKeyEnum : HeaderKeyEnum.values()) {
            String value = exchange.getRequest().getHeaders().getFirst(headerKeyEnum.getName());
            if (ObjectUtil.isEmpty(value) && !headerKeyEnum.getName().equals(HeaderKeyEnum.AUTHORIZATION.getName())) {
                log.info("请求头异常原因：{}", headerKeyEnum.getMsg());
                return FilterCommonUtil.webFluxResponseWriter(exchange.getResponse(), GlobalErrorEnum.签名异常);
            }
            paramMap.put(headerKeyEnum.getName(), value);
        }
        paramMap = getRequestParams(exchange, paramMap);
        if (!verifySign(paramMap)) {
            return FilterCommonUtil.webFluxResponseWriter(exchange.getResponse(), GlobalErrorEnum.签名异常);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }


    /**
     * 获取请求参数
     *
     * @param exchange
     * @param paramMap
     * @return
     */
    public TreeMap<String, Object> getRequestParams(ServerWebExchange exchange, TreeMap<String, Object> paramMap) {
        //get请求参数
        Map<String,String> parmas = exchange.getRequest().getQueryParams().toSingleValueMap();

        if (parmas != null) {
            paramMap.putAll(parmas);
        }
        String body = exchange.getAttribute(FilterCommonUtil.BODY_NAME);
        //body数组处理
        if (ObjectUtil.isNotEmpty(body) && body.startsWith("[")) {
            JSONArray array = JSONArray.parseArray(body);
            for (int i = 0; i < array.size(); i++) {
                try {
                    String value = array.getJSONObject(i).toJSONString();
                    paramMap.put(i + "", value);
                } catch (Exception e) {
                    String value = array.get(i).toString();
                    paramMap.put(i + "", value);
                }
            }
        } else if (ObjectUtil.isNotEmpty(body) && body.startsWith("{")) { //对象处理
            JSONObject json = JSONObject.parseObject(body);
            paramMap.putAll(BeanUtil.beanToMap(json));
        }
        return paramMap;
    }

    /**
     * 签名校验
     *
     * @param paramMap
     */
    public boolean verifySign(TreeMap<String, Object> paramMap) throws IOException {
        String random = (String) paramMap.get(HeaderKeyEnum.RANDOM.getName());
        String timeStr = (String) paramMap.get(HeaderKeyEnum.TIME.getName());
        // 服务端加签密文 = 加签方法(私钥+参数生产);
        String redisRandom = (String) redisService.get(random);
        if (null != redisRandom) {
            log.info("RANDOM:{},在5分中内使用过", random);
            return false;
        }
        //时间搓
        Long time = Long.valueOf(timeStr) + 60L * 1000L;
        if (System.currentTimeMillis() >= time) {
            log.info("time:{},请求发起时间超过一分钟");
            return false;
        }
        redisService.set(random, random, 5L*60);
        //签名校验
        StringBuffer stringBuffer = new StringBuffer();
        paramMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> {
                    if (!HeaderKeyEnum.SIGN.getName().equals(e.getKey()) && !HeaderKeyEnum.AUTHORIZATION.getName().equals(e.getKey())) {
                        if (ObjectUtil.isNotEmpty(e.getValue())) {
                            String value = "";
                            if (e.getValue() instanceof JSONObject) {
                                value = JSONObject.toJSONString(e.getValue());
                            } else if (e.getValue() instanceof JSONArray) {
                                value = JSONObject.toJSONString(e.getValue());
                            } else {
                                value = e.getValue().toString();
                            }
                            stringBuffer.append(e.getKey() + "=" + value);
                            stringBuffer.append("&");
                        }
                    }
                });
        stringBuffer.append(AESUtil.SIGN_KEY);
        String headerSign = AESUtil.decrypt(paramMap.get(HeaderKeyEnum.SIGN.getName()).toString(), AESUtil.SIGN_KEY);
        String signStr = stringBuffer.toString();
        if (!headerSign.equals(signStr)) {
            log.info("headerSign:{}", headerSign);
            log.info("signStr:{}", signStr);
            return false;
        }
        return true;
    }
}
