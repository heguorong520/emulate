
package com.emulate.backend.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class BackendUpdatePwdDTO implements Serializable {

    @NotBlank(message = "原登录密码不能为空")
    @ApiModelProperty("原登录密码")
    private String password;
    @NotBlank(message = "新登录密码不能为空")
    @ApiModelProperty("新登录密码")
    private String newPassword;

}
