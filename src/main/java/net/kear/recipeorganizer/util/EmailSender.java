package net.kear.recipeorganizer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;
	
	public EmailSender() {}

	//public void 
	
}
