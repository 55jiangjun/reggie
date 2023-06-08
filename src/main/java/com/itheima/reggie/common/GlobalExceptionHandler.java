package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器：
 */
@ControllerAdvice(annotations = {Controller.class, RestController.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String mes = s[2]+"已存在";
            return R.error(mes);

        }
        return R.error("未知错误");
    }
    @ExceptionHandler({CustomException.class})
    public R<String> exceptionHandle(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
