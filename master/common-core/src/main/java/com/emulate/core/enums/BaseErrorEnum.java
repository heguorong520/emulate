package com.emulate.core.enums;

public enum BaseErrorEnum {
    默认异常(500,"服务器异常！"),
    数据已存在(5001,"数据已存在！"),
    旧密码错误(5002,"旧密码错误！"),
    用户不存在(5003,"用户不存在错误！"),
    密码错误(5004,"密码错误！"),
    不能删除(5005,"系统数据不能删除！"),
    存在子节点(5006,"请先删除子菜单"),
    未登录(4001,"登录失效");

    BaseErrorEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
