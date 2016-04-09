package net.kear.recipeorganizer.enums;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class ApprovalStatusFormatter implements Formatter<ApprovalStatus> {

	@Autowired
	private MessageSource messages;
	
	public ApprovalStatusFormatter() {}

	@Override
	public String print(ApprovalStatus object, Locale locale) {
		return messages.getMessage("approvalstatus." + object.name().toLowerCase(), null, "", locale);
	}

	@Override
	public ApprovalStatus parse(String text, Locale locale) throws ParseException {
		return ApprovalStatus.parse(text);
	}
}
