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
public class InvitationEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String[] textCodes = {
			"email.invitation.subject",
			"email.invitation.inviteMsg1",
			"email.invitation.inviteMsg2",
			"email.invitation.inviteMsg3",
			"email.invitation.expire",
			"email.invitation.feature1",
			"email.invitation.feature2",
			"email.invitation.feature3",
			"email.invitation.feature4",
			"email.invitation.feature5",
			"email.invitation.feature6",
			"email.invitation.feature7",
			"email.invitation.feature8",
			"email.invitation.feature9",
			"email.invitation.tryit",
			"email.invitation.convenience",
			"email.invitation.login",
			"email.common.optout.type.account",
			"email.common.nextSteps",
			"email.common.pastelink",
			"email.common.enjoy",
			"email.common.tagline",
			"email.common.folks"};

	public InvitationEmail() {}
	
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct InvitationEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);
		
		emailDetail.setSubject(getMsgText("email.invitation.subject"));
	
		Object[] obj = new String[] {null, null};
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("invitationSubject", getMsgText("email.invitation.subject"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = emailDetail.getRecipientName();		
		map.put("dearMember", getArgMessage("email.common.dearUser", obj));
		for (int i=1;i<4;i++) {
			map.put("inviteMsg"+i, getMsgText("email.invitation.inviteMsg"+i));
		}
		for (int i=1;i<10;i++) {
			map.put("feature"+i, getMsgText("email.invitation.feature"+i));
		}
		map.put("tryit", getMsgText("email.invitation.tryit"));
		map.put("convenience", getMsgText("email.invitation.convenience"));
		map.put("login", getMsgText("email.invitation.login"));
		map.put("tokenUrl", getAppUrl() + emailDetail.getTokenUrl());
		map.put("nextSteps", getMsgText("email.common.nextSteps"));
		map.put("pasteLink", getMsgText("email.common.pastelink"));
		map.put("expire", getMsgText("email.invitation.expire"));
		map.put("enjoy", getMsgText("email.common.enjoy"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		obj[0] = getMsgText("email.common.optout.type.account"); 
		obj[1] = getAppUrl() + emailDetail.getOptoutUrl();
		map.put("optout", getArgMessage("email.common.optout.invitation", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("invitation.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}
