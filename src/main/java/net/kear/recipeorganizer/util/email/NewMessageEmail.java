package net.kear.recipeorganizer.util.email;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.kear.recipeorganizer.enums.MessageType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NewMessageEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String[] textCodes = {
			"email.message.title",
			"email.message.admin",
			"email.common.newMessage",
			"email.common.optout.type.newmessage",
			"email.common.tagline",
			"email.common.memberthankyou",
			"email.common.folks"};

	public NewMessageEmail() {}
		
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct NewMessageEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);
		
		emailDetail.setSubject(getMsgText("email.message.title"));
		
		Object[] obj = new String[] {null, null, null};
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("newMessage", getMsgText("email.message.title"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = emailDetail.getRecipientName();
		map.put("dearUser", getArgMessage("email.common.dearUser", obj));

		if (emailDetail.getMessageType() == MessageType.ADMIN) {
			obj[0] = (Object) getMsgText("email.message.admin");
		}
		else
		if (emailDetail.getMessageType() == MessageType.MEMBER) {
			obj[0] = emailDetail.getUserName();
			String str = getArgMessage("email.message.member", obj);
			obj[0] = (Object)str;
		}
		//default to RECIPE
		else {
			obj[0] = emailDetail.getRecipeName();
			String str = getArgMessage("email.message.recipe", obj);
			obj[0] = (Object)str;
		}
		
		Date msgDate = emailDetail.getMessageDate(); 
		DateTime dt = new DateTime(msgDate);
		DateTimeFormatter fmt = DateTimeFormat.fullDate();
		DateTimeFormatter fmtLocale = fmt.withLocale(emailDetail.getLocale());
		obj[1] = fmtLocale.print(dt);
		fmt = DateTimeFormat.shortTime();
		fmtLocale = fmt.withLocale(emailDetail.getLocale());
		obj[2] = fmtLocale.print(dt);
		map.put("newMessageText", getArgMessage("email.message.messageText", obj));
		obj[0] = getAppUrl() + "/user/messages";
		map.put("messageLink", getArgMessage("email.message.link", obj));
		map.put("memberThankyou", getMsgText("email.common.memberthankyou"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		obj[1] = null;
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		obj[0] = getMsgText("email.common.optout.type.newmessage");
		obj[1] = getAppUrl() + emailDetail.getOptoutUrl();
		obj[2] = getAppUrl() + "/user/changeAccount";
		map.put("optout", getArgMessage("email.common.optout", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("newMessage.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}