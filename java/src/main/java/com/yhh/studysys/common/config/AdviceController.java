package com.yhh.studysys.common.config;

import com.yhh.studysys.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author howe
 * @Desc
 * @Date: 2021/2/25 20:10
 */
@RestControllerAdvice
@Slf4j
public class AdviceController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public CommonResult<Object> exceptionHandler(Exception ex){

        if(ex instanceof BusinessException){
            log.error(ex.getMessage());
            return CommonResult.failed("异常提醒 : " + ex.getMessage());
        }else if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException e = (MethodArgumentNotValidException)ex;
            log.error(ex.getMessage());
            return CommonResult.failed("校验错误:" + e.getBindingResult().getFieldError().getDefaultMessage());
        }else {
            ex.printStackTrace();
        }
        return CommonResult.failed();
    }

}
