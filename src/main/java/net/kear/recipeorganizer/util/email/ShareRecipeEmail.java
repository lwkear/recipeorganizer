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
public class ShareRecipeEmail extends EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String[] textCodes = {
			"email.recipe.title",
			"email.recipe.problems",
			"email.recipe.signup",
			"email.recipe.nocost",
			"email.recipe.senderShare",
			"email.common.tagline",
			"email.common.folks"};

	public ShareRecipeEmail() {}
	
	@Override
	public void constructEmail(EmailDetail emailDetail) {
		logger.debug("construct ShareRecipeEmail for: " + emailDetail.getRecipientEmail());

		super.constructEmail(emailDetail);
		setMsgText(textCodes);
		
		emailDetail.setSubject(getMsgText("email.recipe.title"));
		
		Object[] obj = new String[] {null, null};

		Map<String, String> map = new HashMap<String, String>();
		map.put("recipeTitle", getMsgText("email.recipe.title"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = emailDetail.getRecipientName();
		map.put("dearUser", getArgMessage("email.common.dearUser", obj));
		map.put("senderName", emailDetail.getUserName());
		map.put("senderShare", getMsgText("email.recipe.senderShare"));
		map.put("recipeName", emailDetail.getRecipeName());
		obj[0] = emailDetail.getUserFirstName();
		map.put("noteLabel", getArgMessage("email.recipe.noteLabel", obj));
		map.put("userMessage", emailDetail.getUserMessage());
		obj[0] = (Object) env.getProperty("company.email.support.account");
		obj[1] = getAppUrl() + "/contact";
		map.put("problems", getArgMessage("email.recipe.problems", obj));
		map.put("signup", getMsgText("email.recipe.signup"));
		map.put("nocost", getMsgText("email.recipe.nocost"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		obj[1] = null;
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("shareRecipe.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		emailDetail.setBody(out.toString());
	}
}