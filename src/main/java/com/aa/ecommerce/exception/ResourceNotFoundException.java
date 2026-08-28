package com.aa.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException{
    public  ResourceNotFoundException (String msg){
        super(msg);
    }
}
