package com.yhh.studysys.common.config;

/**
 * @author howe
 * @Desc
 * @Date: 2021/2/25 20:11
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = -5867553357310803239L;

    public BusinessException(){super();}

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
