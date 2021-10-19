package com.emulate.backend.controller;

import com.alibaba.fastjson.JSONObject;
import com.emulate.backend.dto.QueryLogDTO;
import com.emulate.cache.annotation.CMPKey;
import com.emulate.cache.annotation.LocalCacheEvent;
import com.emulate.cache.annotation.LocalCachePut;
import com.emulate.cache.enums.ExpireTimeEnum;
import com.emulate.cache.enums.ParamDataTypeEnum;
import com.emulate.cache.redis.repository.CacheKeyRepository;
import com.emulate.cache.redis.service.RedisService;
import com.emulate.core.controller.BaseApiController;
import com.emulate.core.result.ResultBody;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hgr
 * @description: 测试接口
 * @date 2021/10/1915:28
 */
@Api(tags = "测试缓存注解")
@Slf4j
@RestController
public class TestController extends BaseApiController {

   @Autowired
   private RedisService redisService;
    @ResponseBody
    @GetMapping("/sa/list")
    @LocalCachePut(keyPrefix = "testCache", dbLock = true, expire = ExpireTimeEnum.second60)
    public ResultBody<JSONObject> list(
        @ModelAttribute @CMPKey(fields = {"username"}, paramDataType = ParamDataTypeEnum.CDT) QueryLogDTO queryLogDTO) {
        JSONObject data = new JSONObject();
        data.put("key","你好呢宝贝");
        log.info("执行方法");
        return ResultBody.ok(data);
    }

    @LocalCacheEvent(keyPrefix = "testCache")
    @ResponseBody
    @GetMapping("/sa/event")
    public ResultBody<?> list(@CMPKey String userName) {
        return ResultBody.ok();
    }
}
