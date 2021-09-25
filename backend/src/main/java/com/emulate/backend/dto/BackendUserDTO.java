

package com.emulate.backend.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 系统用户
 */
@Data
public class BackendUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "登录名不能为空")
    @ApiModelProperty("登录名")
    private String username;
    @NotBlank(message = "手机号码不能为空")
    @ApiModelProperty("手机号码")
    private String mobile;
    @ApiModelProperty("角色列表")
    private List<Long> roleIdList;
    @ApiModelProperty("用户ID")
    private Long userId;
    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty("昵称")
    private String nickname;
}
