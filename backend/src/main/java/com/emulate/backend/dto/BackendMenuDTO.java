package com.emulate.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class BackendMenuDTO implements Serializable {

    public String target;

    public String icon;

    public String name;

    public String url;

    public String perms;

    public Integer shortcut;

    public Long parentId;

    public List<BackendMenuDTO> child;

    public Long menuId;

    public List<BackendMenuDTO> getChild() {
        if (child == null)
            return Collections.emptyList();
        return child;
    }

    // 冗余字段
    private String parentName;

    public String title;

    public String href;
}
