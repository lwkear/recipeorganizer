package net.kear.recipeorganizer.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.SpeechUtil;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

@Controller
public class SpeechController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired
	SpeechUtil speechUtil;
	@Autowired
	private UserService userService;
	@Autowired
	RecipeService recipeService;
	
	private static final Voice defaultVoiceEN = Voice.EN_ALLISON;
	private static final Voice defaultVoiceFR = Voice.FR_RENEE;
	
	@RequestMapping(value = "/getAudio", method = RequestMethod.GET)
	public void getAudio(@RequestParam("userId") final Long userId, @RequestParam("recipeId") final Long recipeId, 
			@RequestParam("section") final Integer section, @RequestParam("type") final AudioType type, HttpServletResponse response, Locale locale) {
		logger.debug("getAudio");

		Voice voice = defaultVoiceEN; 
		if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
			voice = defaultVoiceFR;
		
		User user = null;
		try {
			user = userService.getUser(userId);
		} 
		catch (Exception ex) {
			//do nothing - this is not a fatal error
		}
		
		if (user != null) {
			UserProfile userProfile = user.getUserProfile();
			if (userProfile != null)
				voice = Voice.getByName(userProfile.getTtsVoice());
		}		
	
		String watsonText = "";
		String extra = "";
		Recipe recipe = recipeService.getRecipe(recipeId);
		
		String voiceLang = voice.getLanguage().substring(0, 2);
		if (!StringUtils.equals(voiceLang, recipe.getLang())) {
			voice = defaultVoiceEN; 
			if (recipe.getLang().equals(new Locale("fr").getLanguage()))
				voice = defaultVoiceFR;
		}

		Date recipeDate;
		if (recipe.getDateUpdated() != null)
			recipeDate = recipe.getDateUpdated();
		else
			recipeDate = recipe.getDateAdded();
		DateTime recipeDateTime = new DateTime(recipeDate);
		
		switch (type) 
		{
		case INGREDIENTS	: watsonText = getIngredText(recipe, section);
							  extra = "." + section;
							  break;
		case INSTRUCTIONS	: watsonText = getInstructText(recipe, section);
							  extra = "." + section;
							  break;
		case NOTES			: watsonText = getNoteText(recipe, null, false);
							  break;
		case PRIVATENOTES	: watsonText = getNoteText(recipe, user, true);
							  extra = "." + userId;
							  break;
		}
		
		String fileName = speechUtil.getSpeechDir() + "recipe" + recipeId + "." + StringUtils.lowerCase(type.name()) + extra + "." + voice.getName() + ".ogg";
		boolean result = speechUtil.getAudio(fileName, watsonText, voice, recipeDateTime, response);
		if (!result) {
			speechUtil.getNoAudioFile(type, voice, response);
		}
	}

	@RequestMapping(value = "/getSample", method = RequestMethod.GET)
	public void getSample(@RequestParam("voiceName") final String voiceName, HttpServletResponse response, Locale locale) {
		logger.debug("getSample");
		
		String fileName = speechUtil.getSpeechDir() + "sample." + voiceName + ".ogg";
		speechUtil.getSample(fileName, response);
	}

	private String getIngredText(Recipe recipe, int section) {
		IngredientSection ingredSection = recipe.getIngredSections().get(section);
		List<RecipeIngredient> ingreds = ingredSection.getRecipeIngredients();
		String text = speechUtil.prepareIngredients(ingreds, 0);
		logger.debug("watson text: " + text);
		return text;
	}
	
	private String getInstructText(Recipe recipe, int section) {
		InstructionSection instructSection = recipe.getInstructSections().get(section);
		List<Instruction> instructs = instructSection.getInstructions();
		String text = speechUtil.prepareInstructions(instructs, 0);
		logger.debug("watson text: " + text);
		return text;
	}

	private String getNoteText(Recipe recipe, User user, boolean privateNotes) {
		String notes = "";
		if (!privateNotes)
			notes = recipe.getNotes();
		else {
			RecipeNote recipeNote = recipeService.getRecipeNote(user.getId(), recipe.getId());
			notes = recipeNote.getNote();
		}
		String text = speechUtil.prepareNotes(notes, 0);
		logger.debug("watson text: " + text);
		return text;
	}
}
