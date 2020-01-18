package com.library.restlibrary.dto;

public class RestException {

	private String errorMessage;

	private Throwable throwable;

	public RestException(String errorMessage, Throwable throwable) {
		super();
		this.errorMessage = errorMessage;
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
