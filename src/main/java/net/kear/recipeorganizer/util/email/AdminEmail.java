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
public class AdminEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String[] textCodes = {
			"email.message.title",
			"email.common.tagline",
			"email.common.folks"};

	public AdminEmail() {}
		
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct NewMessageEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);
		
		emailDetail.setSubject(getMsgText("email.message.title"));
		
		Object[] obj = new String[] {null, null, null};
		
		Map<String, String> map = new HashMap<String, String>();
		//TODO: fix this
		map.put("newMessage", getMsgText("email.message.title"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("userMessage", emailDetail.getUserMessage());
		map.put("originalEmail", emailDetail.getOriginalEmail());
		map.put("recipeOrganizerUrl", getAppUrl());
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		obj[1] = null;
		obj[2] = null;
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("reply.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}