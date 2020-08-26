package com.study.exceptionhandler.exception;

import com.study.exceptionhandler.constant.Status;
import lombok.Getter;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/26
 */
@Getter
public class JsonException extends BaseException{
    public JsonException(Status status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
