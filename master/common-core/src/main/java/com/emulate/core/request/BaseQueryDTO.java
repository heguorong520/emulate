package com.emulate.core.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseQueryDTO implements Serializable {
    private int page;
    private int limit;
}
