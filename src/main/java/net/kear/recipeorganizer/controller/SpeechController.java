package net.kear.recipeorganizer.controller;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.enums.ConversationType;
import net.kear.recipeorganizer.persistence.dto.ConversationDto;
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
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.SpeechUtil;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

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
	private static final Gson GSON = GsonSingleton.getGsonWithoutPrettyPrinting();

	@RequestMapping(value = "/getRecipeAudio", method = RequestMethod.GET)
	public void getRecipeAudio(@RequestParam("userId") final Long userId, @RequestParam("recipeId") final Long recipeId, 
			@RequestParam("section") final Integer section, @RequestParam("type") final AudioType type, HttpServletResponse response, Locale locale) {
		logger.debug("getRecipeAudio:requestparam");

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
			if (userProfile != null) {
				if (userProfile.getTtsVoice() != null)
					voice = Voice.getByName(userProfile.getTtsVoice());
			}
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
		boolean result = speechUtil.getRecipeAudio(fileName, watsonText, voice, recipeDateTime, response);
		if (!result) {
			speechUtil.getNoAudioFile(type, voice, response);
		}
	}

	@RequestMapping(value = "/getAudio", method = RequestMethod.GET)
	public void getAudio(ConversationDto convDto, HttpServletResponse response, Locale locale) {
		logger.debug("getRecipeAudio:requestparam");
		
		speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
	}
	
	@RequestMapping(value = "/getSample", method = RequestMethod.GET)
	public void getSample(@RequestParam("voiceName") final String voiceName, HttpServletResponse response, Locale locale) {
		logger.debug("getSample");
		
		String fileName = speechUtil.getSpeechDir() + "sample." + voiceName + ".ogg";
		speechUtil.getSample(fileName, response);
	}
	
	@RequestMapping(value = "/getWatsonToken", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public String getWatsonToken() {
		logger.info("getWatsonToken");
		
		String token = speechUtil.getSTTToken();
		return token;
	}

	@RequestMapping(value = "/getWatsonKeywords", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<String> getWatsonKeywords(Locale locale) {
		logger.info("getWatsonKeywords GET");

		List<String> keywords = Arrays.asList(speechUtil.getKeywords());
		return keywords;
	}

	/*@RequestMapping(value = "/startWatsonConversation", method = RequestMethod.POST)	//, headers = "x-requested-with=XMLHttpRequest")*/
	@RequestMapping(value = "/startWatsonConversation", method = RequestMethod.GET)	//, headers = "x-requested-with=XMLHttpRequest")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	//public ResponseObject startWatsonConversation(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, HttpServletResponse response, Locale locale) {
	/*public ResponseObject startWatsonConversation(HttpServletResponse response, Locale locale) {*/
	public void startWatsonConversation(HttpServletResponse response, Locale locale) {
		logger.info("startWatsonConversation");

		//ResponseObject obj = new ResponseObject();

		/*MessageRequest msg = speechUtil.startWatsonConversation(userId, recipeId);
		MessageResponse resp = speechUtil.sendWatsonRequest(msg);
		String concatText = resp.getTextConcatenated(":");
		ConversationType type = ConversationType.valueOf(concatText);*/
		//ConversationType type = ConversationType.GREETING;
		//ConversationDto conversationDto = new ConversationDto(userId, recipeId, 0, null, type);
		//obj.setResult((Object)conversationDto);
		
		speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
		
        response.setStatus(HttpServletResponse.SC_OK);
		//return obj;
	}
	
	//Note: several things were needed to get this to work:
	//	the Ajax call must not include contentType: 'application/json; charset=utf-8'
	//	the result object from Watson must be wrapped in an outer Json layer, e.g., {message:data}
	//		(data = stringified result Json object from Watson)
	//	do not use @RequestBody
	//	the Watson result must be captured by the 'receive-json' event, not the 'data' event 
	@RequestMapping(value = "/postWatsonResult", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	/*public ResponseObject postWatsonResult(@RequestParam("message") String message, Locale locale) {*/
	public ResponseObject postWatsonResult(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, @RequestParam("message") String message, 
			HttpServletResponse response, Locale locale) {
		logger.info("postWatsonResult");
		//following code lifted from Watson Java SDK WebSocketManager
		JsonObject json = new JsonParser().parse(message).getAsJsonObject();
		SpeechResults results = null;
		if (json.has("results")) {
			results = GSON.fromJson(message, SpeechResults.class);
		}
		
		if (results == null) {
	        //TODO: SPEECH: get standard error or maybe just throw RestException
	        String msg = "Error extracting STT";
	        ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        return obj;
		}
		
		String speechText = results.getResults().get(0).getAlternatives().get(0).getTranscript();
		logger.debug("results: " + speechText);

		ResponseObject obj = new ResponseObject();
			
		/*if (results != null) {
			logger.debug("results: " + results.getResults().get(0).getAlternatives().get(0).getTranscript());
			
			int keywordCount = 0;
			String keyword = "";
			
			List<String> keywords = Arrays.asList(speechUtil.getKeywords());
			Map<String, List<KeywordsResult>> keywordsResult = results.getResults().get(0).getKeywordsResult();
			if (keywordsResult != null) {
				for (String key : keywordsResult.keySet()) {
					logger.debug("keywords: " + key);
					if (keywords.contains(key)) {
						keywordCount++;
						keyword = key;
					}
				}
			}
			
			if (keywordCount == 1) {
				AudioType type = null;
				
				//private final static String[] keywords = {"ingredient","ingredients","instruction","instructions","note","notes","private"};
				if ((StringUtils.equalsIgnoreCase(keyword, "ingredient")) ||
					(StringUtils.equalsIgnoreCase(keyword, "ingredients")))
					type = AudioType.INGREDIENTS;
				if ((StringUtils.equalsIgnoreCase(keyword, "instruction")) ||
					(StringUtils.equalsIgnoreCase(keyword, "instructions")))
					type = AudioType.INSTRUCTIONS;
				if ((StringUtils.equalsIgnoreCase(keyword, "note")) ||
					(StringUtils.equalsIgnoreCase(keyword, "notes")))
					type = AudioType.NOTES;
				if (StringUtils.equalsIgnoreCase(keyword, "private"))
					type = AudioType.PRIVATENOTES;
					
				responseObj.setResult((String)type.name());
			}
		}*/

        response.setStatus(HttpServletResponse.SC_OK);
		return obj;
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
