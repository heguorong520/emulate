package com.emulate.backend.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class BackendLoginDTO implements Serializable {

    @NotBlank(message = "登录名不能为空")
    @ApiModelProperty("登录名")
    private String username;
    @NotBlank(message = "登录名不能为空")
    @ApiModelProperty("登录密码")
    private String password;
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String code;
}
