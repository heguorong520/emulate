package com.emulate.core.user;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class LoginUserDTO implements Serializable {
    private String username;
    private Long userId;
}
