package com.emulate.permissions.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDetail implements Serializable {

    private String username;

    private Long userId;

}
