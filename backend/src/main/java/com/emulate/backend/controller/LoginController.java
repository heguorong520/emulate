package com.emulate.backend.controller;

import com.emulate.backend.dto.BackendLoginDTO;
import com.emulate.backend.dto.BackendLoginResultDTO;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.result.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = "登录模块")
public class LoginController extends BaseController {
    @Resource
    private BackendUserService backendUserService;


    @ApiOperation("登录")
    @PostMapping("a/login")
    public ResultBody<BackendLoginResultDTO> login(@RequestBody BackendLoginDTO loginDTO) throws Exception {
        return ResultBody.ok(backendUserService.login(loginDTO));
    }

    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public ResultBody<?> logout(@RequestBody BackendLoginDTO loginDTO){
        backendUserService.logout();
        return ResultBody.ok();
    }

    @GetMapping("captcha.jpg")
    public void captcha(HttpServletResponse response)throws IOException {
        /*response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);*/
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("login/info")
    public ResultBody<?> info() {
        return ResultBody.ok(AuthFilter.backendLoginUserDTO());
    }

}
