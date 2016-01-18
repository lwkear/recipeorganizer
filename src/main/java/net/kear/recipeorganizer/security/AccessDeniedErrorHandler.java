package net.kear.recipeorganizer.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AccessDeniedErrorHandler implements AccessDeniedHandler {

	@Autowired
	private MessageSource messages;
	
	public AccessDeniedErrorHandler() {}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
		response.sendRedirect("/recipeorganizer/accessDenied");  
	}
}
