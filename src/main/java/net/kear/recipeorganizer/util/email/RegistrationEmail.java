package net.kear.recipeorganizer.util.email;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
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
			"email.common.expire",
			"email.common.nextSteps",
			"email.common.pastelink",
			"email.common.enjoy",
			"email.common.tagline",
			"email.common.folks"};

	public RegistrationEmail() {
		super();
	}
	
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct RegistrationEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);

		emailDetail.setSubject(getMsgText("email.registration.subject"));
		
		Object[] obj = new String[1];
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("registrationConfirm", getMsgText("email.registration.subject"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = emailDetail.getRecipientName();		
		map.put("welcomeUser", getArgMessage("email.common.welcome", obj));
		map.put("signupThankyou", getMsgText("email.registration.signupThankyou"));
		map.put("completeProcess", getMsgText("email.registration.completeProcess"));
		map.put("tokenUrl", getAppUrl() + emailDetail.getTokenUrl());
		map.put("pasteLink", getMsgText("email.common.pastelink"));
		map.put("expire", getMsgText("email.common.expire"));
		map.put("nextSteps", getMsgText("email.common.nextSteps"));
		map.put("enjoy", getMsgText("email.common.enjoy"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("registrationToken.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}