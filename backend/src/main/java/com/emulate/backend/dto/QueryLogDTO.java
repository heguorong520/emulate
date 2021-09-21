package com.emulate.backend.dto;

import com.emulate.core.request.BaseQueryDTO;
import lombok.Data;

@Data
public class QueryLogDTO extends BaseQueryDTO {
    private String userName;
}
