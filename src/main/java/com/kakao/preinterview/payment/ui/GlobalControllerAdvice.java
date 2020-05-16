package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidCardInfoParamException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCardInfoParamException.class)
    public String handleInvalidCardInfoParamException(InvalidCardInfoParamException exception) {
        return exception.getMessage();
    }
}
