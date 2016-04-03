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
public class AccountChangeEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public enum ChangeType {PASSWORD, PROFILE}; 
	
	private String[] textCodes = {
			"email.password.change",
			"email.profile.change",
			"email.common.accountChange",			
			"email.common.tagline",
			"email.common.notinitiate",
			"email.common.memberthankyou",
			"email.common.folks"};
	private ChangeType changeType;

	public AccountChangeEmail() {
		super();
	}
	
	public void setChangeType(ChangeType type) {
		this.changeType = type;
	}
	
	@Override
	public void constructEmail() {
		logger.debug("construct AccountChangeEmail");

		setMsgText(textCodes);
		setSubject(getMsgText("email.common.accountChange"));
		
		Object[] obj = new String[1];
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("accountChange", getMsgText("email.common.accountChange"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = getRecipientName();		
		map.put("dearUser", getArgMessage("email.common.dearUser", obj));
		if (changeType == ChangeType.PASSWORD)
			map.put("changeText", getMsgText("email.password.change"));
		else
			map.put("changeText", getMsgText("email.profile.change"));
		obj[0] = getAppUrl() + "/contact";
		map.put("notInitiate", getArgMessage("email.common.notinitiate", obj));
		map.put("memberThankyou", getMsgText("email.common.memberthankyou"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("accountChange.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		setBody(out.toString());
	}
}