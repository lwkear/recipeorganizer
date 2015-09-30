package net.kear.recipeorganizer.exception;

/*****
 * Exception thrown by UsersService.addUser if email already exists in USERS table 
*****/

@SuppressWarnings("serial")
public class EmailExistsException extends Throwable {

    public EmailExistsException(final String message) {
        super(message);
    }

}