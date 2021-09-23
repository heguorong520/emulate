package com.emulate.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class NavDTO implements Serializable {

    // "title": "常规管理",
    // "icon": "fa fa-address-book",
    // "href": "",
    //  "target": "_self",
    //  "child":

    public String title;

    public String icon;

    public String href;

    public List<NavDTO> child;

    public Long id;

    public List<NavDTO> getChild() {
        if(child == null)
        return Collections.emptyList();
        return child;
    }
}
