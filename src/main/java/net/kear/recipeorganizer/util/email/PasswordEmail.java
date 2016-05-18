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
public class PasswordEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String[] textCodes = {
			"email.password.reset",
			"email.password.completeProcess",
			"email.common.accountChange",			
			"email.common.pastelink",
			"email.common.tagline",
			"email.common.notinitiate",
			"email.common.memberthankyou",
			"email.common.folks"};
	
	public PasswordEmail() {
		super();
	}
	
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct PasswordEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);
		
		emailDetail.setSubject(getMsgText("email.common.accountChange"));
		
		Object[] obj = new String[] {null, null};
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("accountChange", getMsgText("email.common.accountChange"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = emailDetail.getRecipientName();		
		map.put("dearUser", getArgMessage("email.common.dearUser", obj));
		map.put("passwordReset", getMsgText("email.password.reset"));
		map.put("completeProcess", getMsgText("email.password.completeProcess"));
		map.put("tokenUrl", getAppUrl() + emailDetail.getTokenUrl());
		map.put("pasteLink", getMsgText("email.common.pastelink"));		
		obj[0] = (Object) env.getProperty("company.email.support.account");
		obj[1] = getAppUrl() + "/contact";
		map.put("notInitiate", getArgMessage("email.common.notinitiate", obj));
		map.put("memberThankyou", getMsgText("email.common.memberthankyou"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		obj[1] = null;
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("passwordToken.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}