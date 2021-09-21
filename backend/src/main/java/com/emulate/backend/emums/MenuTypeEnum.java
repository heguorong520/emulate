
package com.emulate.backend.emums;

public enum MenuTypeEnum {
    目录(0,"EMULATE:BACKEND:TOKEN:USER:"),
    菜单(1,"EMULATE:BACKEND:TOKEN:USER:");
    MenuTypeEnum(Integer type, String name){
        this.value = type;
        this.name = name;
    }
    private Integer value;

    private String name;

    public Integer getValue() {
        return value;
    }

    public String getMenuName() {
        return name;
    }
}
