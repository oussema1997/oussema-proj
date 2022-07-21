package com.sfm.obd.dto;

import lombok.Data;

@Data
public class ApiResponse {

	private int status;
	private Object data;
	private String message;
	private String error;
	
	public ApiResponse(Object data, String message, int status) {
		super();
		this.data = data;
		this.message = message;
		this.status = status;
	}
	
	public ApiResponse(String error, String message, int status) {
		super();
		this.error = error;
		this.message = message;
		this.status = status;
	}

}
