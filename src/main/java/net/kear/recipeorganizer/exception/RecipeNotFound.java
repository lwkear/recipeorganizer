package net.kear.recipeorganizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecipeNotFound extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RecipeNotFound() {}

	public RecipeNotFound(String message) {
		super(message);
	}

	public RecipeNotFound(Throwable cause) {
		super(cause);
	}

	public RecipeNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public RecipeNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
