package net.kear.recipeorganizer.enums;

import java.text.ParseException;
import java.util.Locale;

import net.kear.recipeorganizer.enums.ApprovalAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class ApprovalActionFormatter implements Formatter<ApprovalAction> {

	@Autowired
	private MessageSource messages;
	
	public ApprovalActionFormatter() {}

	@Override
	public String print(ApprovalAction object, Locale locale) {
		return messages.getMessage("approvaladmin." + object.name().toLowerCase(), null, "", locale);
	}

	@Override
	public ApprovalAction parse(String text, Locale locale) throws ParseException {
		return ApprovalAction.parse(text);
	}
}
