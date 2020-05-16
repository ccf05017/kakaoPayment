package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidCardInfoParamException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.NotExistInstallmentFormatMonth;
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

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotExistInstallmentFormatMonth.class)
    public String handleNotExistInstallmentFormatMonth() {
        return "Invalid Installment Month";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPayAmountException.class)
    public String handleInvalidPayAmountException() {
        return "Invalid Pay Amount";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTaxAmountException.class)
    public String handleInvalidTaxAmountException() {
        return "Invalid Tax Amount";
    }
}
