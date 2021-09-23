package com.emulate.core.excetion;

import com.emulate.core.enums.GlobalErrorEnum;


/**
 * 自定义异常
 */
public class CustomizeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public CustomizeException(String msg) {
        super(msg);
        this.msg = msg;
    }
    public CustomizeException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }
    public CustomizeException(GlobalErrorEnum errorEnum) {
        super(errorEnum.getMsg());
        this.msg = errorEnum.getMsg();
        this.code = errorEnum.getCode();
    }

    public CustomizeException(GlobalErrorEnum errorEnum, Throwable e) {
        super(errorEnum.getMsg(),e);
        this.msg = errorEnum.getMsg();
        this.code = errorEnum.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
