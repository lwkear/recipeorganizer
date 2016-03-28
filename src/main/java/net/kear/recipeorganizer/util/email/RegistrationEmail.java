package net.kear.recipeorganizer.util.email;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String[] textCodes = {
			"email.registration.subject",
			"email.registration.signupThankyou",
			"email.registration.completeProcess",
			"email.registration.nextSteps",
			"email.common.enjoy",
			"email.common.tagline",
			"email.common.thankyou"};

	public RegistrationEmail() {
		super();
	}
	
	@Override
	public void constructEmail() {
		logger.debug("constructEmail");

		setSubjectCode("email.registration.subject");
		setMsgText(textCodes);
		
		Object[] obj = new String[1];
		
		Map<String, String> map = new HashMap<String, String>();
		obj[0] = getRecipientName();
		map.put("registrationConfirm", getMsgText("email.registration.subject"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		map.put("welcomeUser", getArgMessage("email.common.welcome", obj));
		map.put("signupThankyou", getMsgText("email.registration.signupThankyou"));
		map.put("completeProcess", getMsgText("email.registration.completeProcess"));
		map.put("tokenUrl", getTokenUrl());
		map.put("nextSteps", getMsgText("email.registration.nextSteps"));
		map.put("enjoy", getMsgText("email.common.enjoy"));
		map.put("thankyou", getMsgText("email.common.thankyou"));
		obj[0] = "2016";
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("sendToken.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		setBody(out.toString());
	}
}