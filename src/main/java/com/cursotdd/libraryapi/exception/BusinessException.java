package com.cursotdd.libraryapi.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 6903495761098219887L;
	
	public BusinessException(String message) {
		super(message);
	}

}
