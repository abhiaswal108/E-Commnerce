package com.aa.ecommerce.exception;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private int statusCode;
    private  String msg;
    private  LocalDateTime timeStamp;

    public ErrorResponse(int statusCode,String msg,LocalDateTime time){
        this.statusCode=statusCode;
        this.msg=msg;
        this.timeStamp=time;
    }

}
