package net.kear.recipeorganizer.enums;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class SourceTypeFormatter implements Formatter<SourceType> {

	@Autowired
	private MessageSource messages;
	
	public SourceTypeFormatter() {}

	@Override
	public String print(SourceType object, Locale locale) {
		return messages.getMessage("sourcetype." + object.name(), null, "", locale);
	}

	@Override
	public SourceType parse(String text, Locale locale) throws ParseException {
		return SourceType.parse(text);
	}
}
