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

	private String[] textCodes = {
			"email.recipe.title",
			"email.recipe.problems",
			"email.recipe.signup",
			"email.recipe.nocost",
			"email.recipe.senderShare",
			"email.common.tagline",
			"email.common.folks"};
	private String recipeName;
	private String userMessage;
	private String userFirstName;

	public ShareRecipeEmail() {
		super();
	}
	
	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	
	@Override
	public void constructEmail() {
		logger.debug("construct ShareRecipeEmail");

		setMsgText(textCodes);
		setSubject(getMsgText("email.recipe.title"));
		
		Object[] obj = new String[1];

		Map<String, String> map = new HashMap<String, String>();
		map.put("recipeTitle", getMsgText("email.recipe.title"));		
		map.put("tagline", getMsgText("email.common.tagline"));
		map.put("recipeOrganizerUrl", getAppUrl());
		obj[0] = getRecipientName();
		map.put("dearUser", getArgMessage("email.common.dearUser", obj));
		map.put("senderName", getSenderName());
		map.put("senderShare", getMsgText("email.recipe.senderShare"));
		map.put("recipeName", recipeName);
		obj[0] = userFirstName;
		map.put("noteLabel", getArgMessage("email.recipe.noteLabel", obj));
		map.put("userMessage", userMessage);
		obj[0] = getAppUrl() + "/contact";
		map.put("problems", getArgMessage("email.recipe.problems", obj));
		map.put("signup", getMsgText("email.recipe.signup"));
		map.put("nocost", getMsgText("email.recipe.nocost"));
		map.put("folks", getMsgText("email.common.folks"));
		DateTime now = new DateTime();
		obj[0] = now.toString("yyyy");
		map.put("copyright", getArgMessage("email.common.copyright", obj));
		Writer out = new StringWriter();
		
		try {
			setTemplate("shareRecipe.ftl");
			getTemplate().process(map, out);		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		setBody(out.toString());
	}
}