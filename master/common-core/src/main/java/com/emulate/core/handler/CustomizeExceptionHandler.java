/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.emulate.core.handler;

import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.result.ResultBody;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class CustomizeExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomizeException.class)
    public ResultBody handleRRException(CustomizeException e) {
        ResultBody r = new ResultBody();
        r.setCode(e.getCode());
        r.setMsg(e.getMessage());
        return r;
    }
/*
    @ExceptionHandler(DuplicateKeyException.class)
    public ResultBody handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return ResultBody.error(GlobalErrorEnum.数据已存在);
    }*/


    @ExceptionHandler(Exception.class)
    public ResultBody handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResultBody.error();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultBody handleException(MethodArgumentNotValidException e) {
        log.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), e);
        return ResultBody.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
