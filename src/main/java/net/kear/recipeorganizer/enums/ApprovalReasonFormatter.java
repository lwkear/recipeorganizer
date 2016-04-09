package net.kear.recipeorganizer.enums;

import java.text.ParseException;
import java.util.Locale;

import net.kear.recipeorganizer.enums.ApprovalReason;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class ApprovalReasonFormatter implements Formatter<ApprovalReason> {

	@Autowired
	private MessageSource messages;
	
	@Override
	public String toString() {
		return super.toString();
	}

	public ApprovalReasonFormatter() {}

	@Override
	public String print(ApprovalReason object, Locale locale) {
		return messages.getMessage("approvaladmin." + object.name().toLowerCase(), null, "", locale);
	}

	@Override
	public ApprovalReason parse(String text, Locale locale) throws ParseException {
		return ApprovalReason.parse(text);
	}
}
