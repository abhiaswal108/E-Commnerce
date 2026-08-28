package com.aa.ecommerce.exception;

import lombok.Getter;
import org.w3c.dom.html.HTMLImageElement;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ValidationErrorResponse {
    private int statusCode;
    private String msg;
    private LocalDateTime time;
    private Map<String,String> errors;

    public ValidationErrorResponse(int statusCode,String msg,LocalDateTime time,Map<String,String> errors)
    {
        this.statusCode=statusCode;
        this.msg=msg;
        this.time= time;
        this.errors=errors;
    }
}
