package com.emulate.core.enums;

public enum RedisCacheKeyEnum {
    后台TOKEN(7*24*60*60L,"EMULATE:BACKEND:TOKEN:USER:");
    RedisCacheKeyEnum(Long time, String key){
        this.time = time;
        this.key = key;
    }
    private Long time;

    private String key;

    public Long getCacheTime() {
        return time;
    }

    public String getCacheKey() {
        return key;
    }
}
