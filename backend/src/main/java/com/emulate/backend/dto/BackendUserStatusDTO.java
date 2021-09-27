package com.emulate.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class BackendUserStatusDTO implements Serializable {
    @NotNull(message = "状态不能为空")
    private Integer status;
    @NotNull(message = "用户ID")
    private Long userId;
}
