package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PasswordResendException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PasswordResendException() {}

	public PasswordResendException(String message) {
		super(message);
	}

	public PasswordResendException(Throwable cause) {
		super(cause);
	}

	public PasswordResendException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordResendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
