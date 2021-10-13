package com.emulate.permissions.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDetail implements Serializable {

    private String username;

    private Long userId;

}
