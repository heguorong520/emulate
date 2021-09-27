package com.emulate.backend.dto;

import com.emulate.core.request.BaseQueryDTO;
import lombok.Data;

@Data
public class QueryRoleDTO extends BaseQueryDTO {
    private String roleName;
}
