package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OptOutException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public OptOutException() {}

	public OptOutException(String message) {
		super(message);
	}

	public OptOutException(Throwable cause) {
		super(cause);
	}

	public OptOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public OptOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
