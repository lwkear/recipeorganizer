package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)

public class VerificationResendException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public VerificationResendException() {}

	public VerificationResendException(String message) {
		super(message);
	}

	public VerificationResendException(Throwable cause) {
		super(cause);
	}

	public VerificationResendException(String message, Throwable cause) {
		super(message, cause);
	}

	public VerificationResendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
