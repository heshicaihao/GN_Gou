package com.gionee.client.model.exception;

public class MyErrorException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String TAG = "MyErrorException";
    String msg;

    public MyErrorException(String errorMsg) {
        super(errorMsg);

    }

}
