package com.emulate.backend.controller;

import com.emulate.backend.dto.BackendLoginDTO;
import com.emulate.backend.dto.BackendLoginResultDTO;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.enums.HeaderKeyEnum;
import com.emulate.core.enums.RedisCacheKeyEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.result.ResultBody;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "登录模块")
public class LoginController extends BaseController {
    @Resource
    private BackendUserService backendUserService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DefaultKaptcha producer;


    @ApiOperation("登录")
    @PostMapping("a/login")
    public ResultBody<BackendLoginResultDTO> login(@Valid @RequestBody BackendLoginDTO loginDTO) throws Exception {
        String key = RedisCacheKeyEnum.CAPTCHA_KEY.getCacheKey()+HeaderKeyEnum.DEVICEID.value();
        String code = (String) redisTemplate.opsForValue().get(key);
        if(!loginDTO.getCode().equals(code)){
            throw new CustomizeException("验证码错误!");
        }
        return ResultBody.ok(backendUserService.login(loginDTO));
    }

    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public ResultBody<?> logout(){
        backendUserService.logout();
        return ResultBody.ok();
    }

    @GetMapping("sa/captcha.jpg")
    public void captcha(String deviceId,HttpServletResponse response, HttpServletRequest request)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        redisTemplate.opsForValue().set(RedisCacheKeyEnum.CAPTCHA_KEY.
                getCacheKey()+deviceId,text,RedisCacheKeyEnum.CAPTCHA_KEY.getCacheTime(), TimeUnit.SECONDS);
        //生成图片验证码
        BufferedImage image = producer.createImage(text);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("login/info")
    public ResultBody<?> info() {
       // AuthFilter.backendLoginUserDTO()
        return ResultBody.ok();
    }

}
