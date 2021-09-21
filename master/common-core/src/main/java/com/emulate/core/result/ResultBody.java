package com.emulate.core.result;

import com.emulate.core.enums.BaseErrorEnum;
import lombok.Data;

/**
 *API接口实体
 */
@Data
public  class  ResultBody<T> {


    private  T data;

    private int code = 200;

    private String msg = "操作成功";

    public static ResultBody error(BaseErrorEnum e){
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(e.getCode());
        resultBody.setMsg(e.getMsg());
        return resultBody;
    }

    public static ResultBody error(){
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(BaseErrorEnum.默认异常.getCode());
        resultBody.setMsg(BaseErrorEnum.默认异常.getMsg());
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
}
