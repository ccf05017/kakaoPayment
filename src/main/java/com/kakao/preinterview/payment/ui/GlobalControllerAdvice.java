package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.payment.exceptions.*;
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

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotExistPaymentHistoryException.class)
    public String handleNotExistPaymentHistoryException() {
        return "Not Exist Such PaymentHistory Data";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPayCancelAmountException.class)
    public String handleInvalidPayCancelAmountException() {
        return "Invalid Payment Cancel Amount";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TryCancelFromCanceledPaymentException.class)
    public String handleTryCancelFromCanceledPaymentException() {
        return "Already Canceled";
    }
}
