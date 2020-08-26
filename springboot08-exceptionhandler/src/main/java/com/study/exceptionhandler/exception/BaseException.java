package com.study.exceptionhandler.exception;

import com.study.exceptionhandler.constant.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException{
    private Integer code;
    private String message;

    public BaseException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
