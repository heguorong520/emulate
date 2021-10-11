package com.emulate.core.aspect;

import com.emulate.core.annotation.Log;
import com.emulate.core.utils.HttpRequestUtil;
import com.emulate.core.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class LogServiceAspect {
    @Pointcut("@annotation(com.emulate.core.annotation.Log)")
    public void logPointCut() {

    }
    //@annotation(com.emulate.core.annotation.Log)
    @Around("logPointCut()")
    public  Object  addLog(ProceedingJoinPoint jp) throws Throwable {
        Object result;
        //获取方法参数
        MethodSignature signature = (MethodSignature) jp.getSignature();
        //获取方法地址
        Method method1 = signature.getMethod();
        //获取地址内容，即名称
        String method = method1.getName();
//        //获取切入点@Log注解
//        Log annotation = method1.getAnnotation(Log.class);
//        //获取注解内容
//        String description = annotation.value();
        //访问来源ip
        String ip = IPUtils.getIpAddr();
        //准备参数
        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = jp.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature)jp.getSignature()).getParameterNames();
        //对应放一起
        if(argValues != null){
            for (int i = 0; i < argValues.length; i++) {
                params.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }
        String  parameters = params.toString()+"}";
        //切入时间
        long befTime = System.currentTimeMillis();
        //运行切入对象，拦截返回值
        result = jp.proceed();
        //获取当前时间
        long nowTime = System.currentTimeMillis();
        //转换时间格式
        Date date = new Date(nowTime);
        //运行时间
        Long cost = nowTime-befTime;
        //保存日志
        //logService.addLog(method,ip,parameters,description,cost,date);
        //将拦截的返回值传到前端
        String url = HttpRequestUtil.getHttpServletRequest().getRequestURL().toString();
        log.info("接口地址:{}",url);
        log.info("接口参数:{}",parameters);
        log.info("接口返回结果:{}",result);
        log.info("接口响应时间毫秒:{}",cost);
        return result;
    }

}
