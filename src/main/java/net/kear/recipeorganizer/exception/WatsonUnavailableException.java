package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class WatsonUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public WatsonUnavailableException() {}

	public WatsonUnavailableException(String message) {
		super(message);
	}

	public WatsonUnavailableException(Throwable cause) {
		super(cause);
	}

	public WatsonUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public WatsonUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
