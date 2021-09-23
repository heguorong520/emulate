package com.emulate.core.result;

import com.emulate.core.enums.GlobalErrorEnum;
import lombok.Data;

/**
 *API接口实体
 */
@Data
public  class  ResultBody<T> {


    private  T data;

    private int code = 200;

    private String msg = "操作成功";

    public static ResultBody error(GlobalErrorEnum e){
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(e.getCode());
        resultBody.setMsg(e.getMsg());
        return resultBody;
    }

    public static ResultBody error(){
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(GlobalErrorEnum.默认异常.getCode());
        resultBody.setMsg(GlobalErrorEnum.默认异常.getMsg());
        return resultBody;
    }

    public static<T> ResultBody ok(){
        ResultBody resultBody = new ResultBody();
        return resultBody;
    }

    public static<T>  ResultBody ok(T data){
        ResultBody resultBody = new ResultBody();
        resultBody.setData(data);
        return resultBody;
    }
    public static ResultBody error(String msg){
        ResultBody resultBody = new ResultBody();
        resultBody.setMsg(msg);
        return resultBody;
    }

}
