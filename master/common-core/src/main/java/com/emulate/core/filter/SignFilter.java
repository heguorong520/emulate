//package com.emulate.core.filter;
//
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.emulate.core.enums.GlobalErrorEnum;
//import com.emulate.core.enums.HeaderKeyEnum;
//import com.emulate.core.utils.AESUtil;
//import com.emulate.core.yml.AuthSignYml;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * 签名验证
// */
//@Slf4j
//public class SignFilter extends BaseFilter {
//
//    private RedisTemplate redisTemplate;
//    private AuthSignYml authSignYml;
//
//    public SignFilter(RedisTemplate redisTemplate, AuthSignYml authSignYml) {
//        this.redisTemplate = redisTemplate;
//        this.authSignYml = authSignYml;
//    }
//

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        if (!authSignYml.getEnableSign() || verifyPath(request.getServletPath(), authSignYml.getNoSignList())) {
//            chain.doFilter(request, response);
//            return;
//        }
//        TreeMap<String, Object> paramMap = new TreeMap<>();
//        //校验请求头参数
//        for (HeaderKeyEnum headerKeyEnum : HeaderKeyEnum.values()) {
//            String value = request.getHeader(headerKeyEnum.getName());
//            if (ObjectUtil.isEmpty(value) && !headerKeyEnum.getName().equals(HeaderKeyEnum.AUTHORIZATION.getName())) {
//                log.info("请求头异常原因：{}", headerKeyEnum.getMsg());
//                this.writeError(response,GlobalErrorEnum.签名异常);
//                return;
//            }
//            paramMap.put(headerKeyEnum.getName(), value);
//        }
//        try{
//            request = getRequestParams(request, paramMap);
//        }catch (Exception e){
//            log.info("获取参数异常原因：{}", e);
//        }
//        if(!verifySign(response, paramMap)){
//            this.writeError(response,GlobalErrorEnum.签名异常);
//            return;
//        }
//        chain.doFilter(request, response);
//    }
//
//
//    /**
//     * 签名校验
//     *
//     * @param paramMap
//     */
//    public boolean verifySign(HttpServletResponse response, TreeMap<String, Object> paramMap) throws IOException {
//        String random = (String) paramMap.get(HeaderKeyEnum.RANDOM.getName());
//        String timeStr = (String) paramMap.get(HeaderKeyEnum.TIME.getName());
//        // 服务端加签密文 = 加签方法(私钥+参数生产);
//        String redisRandom = (String) redisTemplate.opsForValue().get(random);
//        if (null != redisRandom) {
//            log.info("RANDOM:{},在5分中内使用过", random);
//            return false;
//        }
//
//        redisTemplate.opsForValue().set(random, random, 5, TimeUnit.MINUTES);
//        //时间搓
//        Long time = Long.valueOf(timeStr) + 60L * 1000L;
//        if (System.currentTimeMillis() >= time) {
//            log.info("time:{},请求发起时间超过一分钟");
//            return false;
//        }
//        redisTemplate.opsForValue().set(random, random);
//        //签名校验
//        StringBuffer stringBuffer = new StringBuffer();
//        paramMap.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .forEachOrdered(e -> {
//                    if (!HeaderKeyEnum.SIGN.getName().equals(e.getKey()) && !HeaderKeyEnum.AUTHORIZATION.getName().equals(e.getKey())) {
//                        if(ObjectUtil.isNotEmpty(e.getValue())) {
//                            String value = "";
//                            if(e.getValue() instanceof JSONObject ){
//                                value = JSONObject.toJSONString(e.getValue());
//                            }else if(e.getValue() instanceof JSONArray){
//                                value = JSONObject.toJSONString(e.getValue());
//                            }else{
//                                value =  e.getValue().toString();
//                            }
//                            stringBuffer.append(e.getKey() + "=" + value);
//                            stringBuffer.append("&");
//                        }
//                    }
//                });
//        stringBuffer.append(AESUtil.SIGN_KEY);
//        String headerSign = AESUtil.decrypt(paramMap.get(HeaderKeyEnum.SIGN.getName()).toString(), AESUtil.SIGN_KEY);
//        String signStr = stringBuffer.toString();
//        if (!headerSign.equals(signStr)) {
//            log.info("headerSign:{}", headerSign);
//            log.info("signStr:{}", signStr);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 获取body与URL参数
//     *
//     * @param request
//     * @param paramMap
//     * @throws IOException
//     */
//    public HttpServletRequest getRequestParams(HttpServletRequest request, TreeMap<String, Object> paramMap) throws IOException {
//        //URL参数获取
//        Enumeration<String> paramNames = request.getParameterNames();
//        while (request.getParameterNames().hasMoreElements()) {
//            try {
//                String name = paramNames.nextElement();
//                if (name != null) {
//                    paramMap.put(name, request.getParameter(name));
//                }
//            }catch (Exception e){
//                break;
//            }
//        }
//        //Body参数获取
//        if (request.getInputStream() != null) {
//            String body = IOUtils.toString(request.getInputStream(), "utf-8");
//            //body数组处理
//            if (ObjectUtil.isNotEmpty(body) && body.startsWith("[")) {
//                JSONArray array = JSONArray.parseArray(body);
//                for(int i=0; i<array.size();i++){
//                    try{
//                        String value = array.getJSONObject(i).toJSONString();
//                        paramMap.put(i+"",value);
//                    }catch (Exception e){
//                        String value = array.get(i).toString();
//                        paramMap.put(i+"",value);
//                    }
//                }
//            }else if(ObjectUtil.isNotEmpty(body) && body.startsWith("{")){ //对象处理
//                JSONObject json = JSONObject.parseObject(body);
//                paramMap.putAll(BeanUtil.beanToMap(json));
//            }
//            //body里面除了对象和数组加入签名算法其他的不加入
//            //防止body丢失
//            return new MyHttpServletRequest(request, body);
//        }
//        return request;
//    }
//
//
//}
