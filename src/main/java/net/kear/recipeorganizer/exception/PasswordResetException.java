package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PasswordResetException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public PasswordResetException() {}

	public PasswordResetException(String message) {
		super(message);
	}

	public PasswordResetException(Throwable cause) {
		super(cause);
	}

	public PasswordResetException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordResetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
