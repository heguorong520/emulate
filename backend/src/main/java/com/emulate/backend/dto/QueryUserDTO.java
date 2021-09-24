package com.emulate.backend.dto;

import com.emulate.core.request.BaseQueryDTO;
import lombok.Data;

@Data
public class QueryUserDTO extends BaseQueryDTO {
    private String username;
    private String nickname;
    private String mobile;
}
