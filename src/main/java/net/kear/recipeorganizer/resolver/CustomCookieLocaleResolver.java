package net.kear.recipeorganizer.resolver;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Component
public class CustomCookieLocaleResolver extends CookieLocaleResolver {

	@Override
	protected Locale determineDefaultLocale(HttpServletRequest request) {
		
		String lang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        if (StringUtils.isBlank(lang)) {
            return super.determineDefaultLocale(request);
        }
		
		return request.getLocale();
	}

	@Override
	public void setDefaultLocale(Locale defaultLocale) {
		Locale locale = new Locale("en");
		super.setDefaultLocale(locale);
	}
}
