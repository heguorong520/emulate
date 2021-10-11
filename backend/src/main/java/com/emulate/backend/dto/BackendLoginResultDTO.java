package com.emulate.backend.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class BackendLoginResultDTO implements Serializable {

    @ApiModelProperty("登录名")
    private String username;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("登录凭证token")
    private String token;

    @ApiModelProperty("接口权限")
    private List<String> perms;
}
