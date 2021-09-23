package com.emulate.core.filter;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.util.AESUtil;
import com.emulate.core.yml.AuthSignYml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 签名验证
 */
@Slf4j
public class SignFilter extends BaseFilter {

    private RedisTemplate redisTemplate;
    private AuthSignYml authSignYml;

    public SignFilter(RedisTemplate redisTemplate, AuthSignYml authSignYml) {
        this.redisTemplate = redisTemplate;
        this.authSignYml = authSignYml;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!authSignYml.getEnableSign() || verifyPath(request.getServletPath(), authSignYml.getNoSignList())) {
            chain.doFilter(request, response);
            return;
        }
        try {
            TreeMap<String, Object> paramMap = verifyHeadAndGetHeadParams(request, response);
            getRequestParams(request, paramMap);
            verifySign(response, paramMap);
            chain.doFilter(request, response);
        }catch (Exception e){
            return;
        }
    }


    /**
     * 签名校验
     *
     * @param paramMap
     */
    public void verifySign(HttpServletResponse response, TreeMap<String, Object> paramMap) throws IOException {
        String random = (String) paramMap.get(HeaderKeyEnum.RANDOM.getName());
        String timeStr = (String) paramMap.get(HeaderKeyEnum.TIME.getName());
        // 服务端加签密文 = 加签方法(私钥+参数生产);
        String redisRandom = (String) redisTemplate.opsForValue().get(random);
        if (null != redisRandom) {
            log.info("RANDOM:{},在5分中内使用过", random);
            this.writeError(response, GlobalErrorEnum.签名异常);
        }

        redisTemplate.opsForValue().set(random, random, 5, TimeUnit.MINUTES);
        //时间搓
        Long time = Long.valueOf(timeStr) + 60L * 1000L;
        if (System.currentTimeMillis() >= time) {
            log.info("time:{},请求发起时间超过一分钟");
            this.writeError(response, GlobalErrorEnum.签名异常);
        }
        redisTemplate.opsForValue().set(random, random);
        //签名校验
        StringBuffer stringBuffer = new StringBuffer();
        paramMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> {
                    if (!HeaderKeyEnum.SIGN.getName().equals(e.getKey()) || !HeaderKeyEnum.AUTHORIZATION.getName().equals(e.getKey())) {
                        stringBuffer.append(e.getKey() + "=" + e.getValue());
                        stringBuffer.append("&");
                    }
                });
        stringBuffer.append(AESUtil.SIGN_KEY);
        String headerSign = AESUtil.decrypt(paramMap.get(HeaderKeyEnum.SIGN.getName()).toString(), AESUtil.SIGN_KEY);
        String signStr = stringBuffer.toString();
        if (!headerSign.equals(signStr)) {
            log.info("headerSign:{}", headerSign);
            log.info("signStr:{}", signStr);
            this.writeError(response, GlobalErrorEnum.签名异常);
        }
    }

    /**
     * 校验请求头数据并获取请求头数据
     *
     * @param request
     * @return
     */
    public TreeMap<String, Object> verifyHeadAndGetHeadParams(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TreeMap<String, Object> paramMap = new TreeMap<>();
        //校验请求头参数
        for (HeaderKeyEnum headerKeyEnum : HeaderKeyEnum.values()) {
            String value = request.getHeader(headerKeyEnum.getName());
            if (ObjectUtil.isEmpty(value)) {
                log.info("请求头异常原因：{}",headerKeyEnum.getMsg());
                this.writeError(response, GlobalErrorEnum.签名异常);
            }
            paramMap.put(headerKeyEnum.getName(), value);
        }
        return paramMap;
    }

    /**
     * 获取body与URL参数
     *
     * @param request
     * @param paramMap
     * @throws IOException
     */
    public void getRequestParams(HttpServletRequest request, TreeMap<String, Object> paramMap) throws IOException {
        //URL参数获取
        Enumeration<String> paramNames = request.getParameterNames();
        while (request.getParameterNames().hasMoreElements()) {
            String name = paramNames.nextElement();
            paramMap.put(name, request.getParameter(name));
        }
        //Body参数获取
        if (request.getInputStream() != null) {
            String body = IOUtils.toString(request.getInputStream(), "utf-8");
            if (ObjectUtil.isNotEmpty(body)) {
                JSONObject json = JSONObject.parseObject(body);
                paramMap.putAll(BeanUtil.beanToMap(json));
            }
            //防止body丢失
            request = new MyHttpServletRequest(request, body);
        }
    }


}
