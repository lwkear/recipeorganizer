package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class RememberMeSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private AuthCookie authCookie;
	
	public RememberMeSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		authCookie.setCookie(request, response, authentication.getName());
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
