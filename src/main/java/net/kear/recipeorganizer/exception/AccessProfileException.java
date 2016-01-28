package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class AccessProfileException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public AccessProfileException() {}

	public AccessProfileException(String message) {
		super(message);
	}

	public AccessProfileException(Throwable cause) {
		super(cause);
	}

	public AccessProfileException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
