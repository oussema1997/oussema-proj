package com.sfm.obd.exception;

public class ErrorException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    public ErrorException(String message) {
        super(message);
    }
}
