package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class AccessUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AccessUserException() {}

	public AccessUserException(String message) {
		super(message);
	}

	public AccessUserException(Throwable cause) {
		super(cause);
	}

	public AccessUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
