package com.emulate.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    //私钥
    private static final String TOKEN_SECRET_BACKEND = "your private key";
    //私钥
    private static final String TOKEN_SECRET_CLIENT = "your private key";

    //默认有效期30天
    private static final long EXPIRE_TIME = 7*24*60*60*1000;

    /**
     *  生成客户端TOKEN
     * @param json
     * @return
     */
    public static String createTokenClient(String json) {
        return createToken(json,TOKEN_SECRET_CLIENT);
    }

    /**
     * 验证后台token的正确性，并且解析成用户id
     * @param **token**
     * @return
     */
    public static String verifyTokenClient(String token){
        return verifyToken(token,TOKEN_SECRET_CLIENT);
    }

    /**
     *  生成后台TOKEN
     * @param json
     * @return
     */
    public static String createTokenBackend(String json) {
        return createToken(json,TOKEN_SECRET_BACKEND);
    }

    /**
     * 验证后台token的正确性，并且解析成用户id
     * @param **token**
     * @return
     */
    public static String verifyTokenBackend(String token){
        return verifyToken(token,TOKEN_SECRET_BACKEND);
    }


    /*
     * 根据用户信息JSON生成签名
     * @param json  用户相关信息
     * @return
     */
    public static String createToken(String json,String secret) {
        try {
            // 设置过期时间  默认30天
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("account", json)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 验证token的正确性，并且解析成用户id
     * @param **token**
     * @return
     */
    private static String verifyToken(String token,String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String account = jwt.getClaim("account").asString();
            return account;
        } catch (Exception e){
            return null;
        }
    }

/*    public static void main(String[] args) {
        String token = createTokenBackend("user=nihao");
        System.out.println(token);
        String account =  verifyTokenBackend(token);
        System.out.println(account);
    }*/
}
