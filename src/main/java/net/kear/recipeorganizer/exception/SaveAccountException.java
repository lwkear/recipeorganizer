package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SaveAccountException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SaveAccountException() {}

	public SaveAccountException(String message) {
		super(message);
	}

	public SaveAccountException(Throwable cause) {
		super(cause);
	}

	public SaveAccountException(String message, Throwable cause) {
		super(message, cause);
	}

	public SaveAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
