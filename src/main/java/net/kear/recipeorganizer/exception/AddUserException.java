package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class AddUserException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public AddUserException() {}

	public AddUserException(String message) {
		super(message);
	}

	public AddUserException(Throwable cause) {
		super(cause);
	}

	public AddUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
