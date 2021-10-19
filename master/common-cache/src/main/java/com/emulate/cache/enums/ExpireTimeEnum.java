package com.emulate.cache.enums;

/**
 * @author hgr
 * @description: 过期时间定义
 * @date 2021/10/18 23:53
 */
public enum ExpireTimeEnum {
    second5(5),
    second10(10),
    second15(15),
    second20(20),
    second30(30),
    second40(40),
    second50(50),
    second60(60);
    private int expire;
    ExpireTimeEnum(int expire){
        this.expire = expire;
    }

    public int time() {
        return expire;
    }
}
