package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.enums.ConversationType;
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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
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
	@Autowired
	private MessageSource messages;
	
	private static final Voice defaultVoiceEN = Voice.EN_ALLISON;
	private static final Voice defaultVoiceFR = Voice.FR_RENEE;
	private static final Gson GSON = GsonSingleton.getGsonWithoutPrettyPrinting();

	//this method is used for users who use the VCR buttons to play sections of the recipe
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
	
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			//return error in null check
		}
		
		if (recipe == null) {
			//TODO: SPEECH: return default sorry audio
		}
		
		/*
		String watsonText = "";
		String extra = "";

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

		//TODO: SPEECH: handle empty watsonText, e.g., no private notes vs. can't access the user or recipe - different default audios
		
		String fileName = speechUtil.getSpeechDir() + "recipe" + recipeId + "." + StringUtils.lowerCase(type.name()) + extra + "." + voice.getName() + ".ogg";
		boolean result = speechUtil.getRecipeAudio(fileName, watsonText, voice, recipeDateTime, response);*/
		
		boolean result = getRecipeElement(voice, recipe, user, section, type, response, locale);		
		if (!result) {
			speechUtil.getNoAudioFile(type, voice, response);
		}
	}

	/*@RequestMapping(value = "/getAudio", method = RequestMethod.GET)
	public void getAudio(ConversationDto convDto, HttpServletResponse response, Locale locale) {
		logger.debug("getRecipeAudio:requestparam");
		
		speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
	}*/
	
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
		//TODO: SPEECH: error handling?
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

	/*@RequestMapping(value = "/getAudio", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public void getAudio(@RequestParam("audioId") Long audioId, HttpServletResponse response, Locale locale) {
		audioUtil.getNoAudioFile(audioId, response);
	}*/
	
	/*@RequestMapping(value = "/startWatsonConversation", method = RequestMethod.POST)	//, headers = "x-requested-with=XMLHttpRequest")
	@RequestMapping(value = "/startWatsonConversation", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})	//, headers = "x-requested-with=XMLHttpRequest")
	//@ResponseBody
	//@ResponseStatus(value=HttpStatus.OK)
	public void startWatsonConversation(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, HttpServletResponse response, Locale locale) {
	public ResponseObject startWatsonConversation(HttpServletResponse response, Locale locale) {
	public void startWatsonConversation(@RequestParam("name") String name, HttpServletResponse response, Locale locale) {
	public void startWatsonConversation(HttpServletResponse response, Locale locale) {
		logger.info("startWatsonConversation");

		//ResponseObject obj = new ResponseObject();

		MessageRequest msg = speechUtil.startWatsonConversation(userId, recipeId);
		MessageResponse resp = speechUtil.sendWatsonRequest(msg);
		String concatText = resp.getTextConcatenated(":");
		ConversationType type = ConversationType.valueOf(concatText);
		//ConversationType type = ConversationType.GREETING;
		//ConversationDto conversationDto = new ConversationDto(userId, recipeId, 0, null, type);
		//obj.setResult((Object)conversationDto);
		
		speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
		//speechUtil.getAudio(name, response);
		//response.setContentType("audio/ogg");
		//response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		//HttpHeaders header = new HttpHeaders();
		//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, header.);
		//header.setContentType(new MediaType("audio","vnd.ogg"));
        //response.setStatus(HttpServletResponse.SC_OK);
		//return obj;
	}*/
	
	@RequestMapping(value = "/startWatsonConversation", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ResponseStatus(value=HttpStatus.OK)
	public void startWatsonConversation(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, HttpServletResponse response, 
			HttpSession session, Locale locale) {
		logger.info("startWatsonConversation");

		Voice voice = defaultVoiceEN;
		if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
			voice = defaultVoiceFR;
		
		User user = null;
		try {
			user = userService.getUser(userId);
		} 
		catch (Exception ex) {
			//return error in the null check
		}

		if (user != null) {
			UserProfile userProfile = user.getUserProfile();
			if (userProfile != null) {
				if (userProfile.getTtsVoice() != null)
					voice = Voice.getByName(userProfile.getTtsVoice());
			}
		}		
		
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			//return error in the null check
		}
		
		if (user == null || recipe == null || !speechUtil.isWatsonConvAvailable() || !speechUtil.isWatsonSTTAvailable()) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}		
		
		//get the recipe info for the conversation context map
		Map<String, Object> contextMap = setContextMap(recipe);
		MessageRequest req = speechUtil.startWatsonConversation(contextMap);	
		MessageResponse resp = speechUtil.sendWatsonRequest(req);

		String command = null;
		if (resp.getOutput() != null) {
			String outputText = resp.getTextConcatenated(";");
			command = StringUtils.substringBefore(outputText, ";");
			if (!StringUtils.equalsIgnoreCase(command, ConversationType.GREETING.name()))
				command = null;
		}			
		
		if (StringUtils.isBlank(command)) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		Object[] obj = new Object[] {null,null};
		obj[0] = user.getFirstName();
		obj[1] = recipe.getName();
		String msg = messages.getMessage("watson.greeting", obj, "Hello", locale);

		session.setAttribute("watsonContext", resp.getContext());
		session.setAttribute("watsonVoice", voice);
		boolean result = speechUtil.getAudio(msg, voice, "", false, response);
		if (!result) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}	

	//Note: several things were needed to get this to work:
	//	the Ajax call must not include contentType: 'application/json; charset=utf-8'
	//	the result object from Watson must be wrapped in an outer Json layer, e.g., {message:data}
	//		(data = stringified result Json object from Watson)
	//	do not use @RequestBody
	//	the Watson result must be captured by the 'receive-json' event, not the 'data' event
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/postWatsonResult", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ResponseStatus(value=HttpStatus.OK)
	public void postWatsonResult(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, @RequestParam("message") String message, 
			HttpServletResponse response, HttpSession session, Locale locale) {
		logger.info("postWatsonResult");

		//following code lifted from Watson Java SDK WebSocketManager
		JsonObject json = new JsonParser().parse(message).getAsJsonObject();
		SpeechResults results = null;
		if (json.has("results")) {
			results = GSON.fromJson(message, SpeechResults.class);
		}
		
		if (results == null) {
	        //TODO: SPEECH: get standard error or maybe just throw RestException
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        return;
		}

		User user = null;
		try {
			user = userService.getUser(userId);
		} 
		catch (Exception ex) {
			//return error in the null check
		}
		
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			//return error in the null check
		}
		
		if (user == null || recipe == null || !speechUtil.isWatsonConvAvailable() || !speechUtil.isWatsonSTTAvailable()) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}		
	
		String speechText = results.getResults().get(0).getAlternatives().get(0).getTranscript();
		logger.debug("text: " + speechText);
		speechText = speechText.trim();

		Map<String, Object> context = (Map<String, Object>)session.getAttribute("watsonContext");
		MessageRequest req = new MessageRequest.Builder()
			.context(context)
			.inputText(speechText)
			.build();
		MessageResponse resp = speechUtil.sendWatsonRequest(req);
		session.setAttribute("watsonContext", resp.getContext());
		
		String command = null;
		if (resp.getOutput() != null) {
			String outputText = resp.getTextConcatenated(";");
			command = StringUtils.substringBefore(outputText, ";");
		}
		logger.debug("command: " + command);
		
		if (StringUtils.isBlank(command)) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		Voice voice = (Voice)session.getAttribute("watsonVoice");
		ConversationType type = ConversationType.valueOf(command);
		List<Entity> entities = resp.getEntities();
		boolean result = translateWatsonCommand(type, entities, recipe, user, voice, response, locale);
		if (!result) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	//Note: several things were needed to get this to work:
	//	the Ajax call must not include contentType: 'application/json; charset=utf-8'
	//	the result object from Watson must be wrapped in an outer Json layer, e.g., {message:data}
	//		(data = stringified result Json object from Watson)
	//	do not use @RequestBody
	//	the Watson result must be captured by the 'receive-json' event, not the 'data' event 
	/*@RequestMapping(value = "/postWatsonResult", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
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
	 */			
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

    /*    response.setStatus(HttpServletResponse.SC_OK);
		return obj;
	}*/

	private boolean getRecipeElement(Voice voice, Recipe recipe, User user, int section, AudioType type, HttpServletResponse response, Locale locale) {
		String watsonText = "";
		String extra = "";
		long userId = user.getId();
		long recipeId = recipe.getId();
		
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

		//TODO: SPEECH: handle empty watsonText, e.g., no private notes vs. can't access the user or recipe - different default audios
		
		String fileName = speechUtil.getSpeechDir() + "recipe" + recipeId + "." + StringUtils.lowerCase(type.name()) + extra + "." + voice.getName() + ".ogg";
		return (speechUtil.getRecipeAudio(fileName, watsonText, voice, recipeDateTime, response));
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
			if (user != null) {
				RecipeNote recipeNote = recipeService.getRecipeNote(user.getId(), recipe.getId());
				notes = recipeNote.getNote();
			}
		}
		String text = speechUtil.prepareNotes(notes, 0);
		logger.debug("watson text: " + text);
		return text;
	}
	
	private Map<String, Object> setContextMap(Recipe recipe) {
		Map<String, Object> contextMap = new HashMap<String, Object>();
		int ingredCount = recipe.getIngredSections().size();
		int instructCount = recipe.getInstructSections().size();		
		contextMap.put("ingredSetCount", ingredCount);
		contextMap.put("instructSetCount", instructCount);
		
		String ingredNameList = "";
		String instructNameList = "";
		
		if (ingredCount > 1) {
			String names = "";
			String connector = "|";
			String connStr = "";
			//for (IngredientSection sect : recipe.getIngredSections()) {
				//String name = StringUtils.replaceChars(sect.getName(), ' ', '|');
				//ingredNameList = ingredNameList + name + "|" + sect.getName() + "|";
			//}
			for (IngredientSection sect : recipe.getIngredSections()) {
				String[] words = StringUtils.split(sect.getName());
				if (words.length > 1) {
					for (int i=0;i<words.length;i++) {
						names += connStr + words[i];
						connStr = connector;
					}
					if (words.length > 2) {
						for (int i=0;i<(words.length-1);i++) {
							names += connStr + words[i] + " " + words[i+1];
						}
					}
					names += connStr + sect.getName(); 
				}
				else
					names = sect.getName();
			}
			String[] nameArray = StringUtils.split(names, "|");
			List<String> dupeList = Arrays.asList(nameArray);
			List<String> dedupeList = new ArrayList<String>(new LinkedHashSet<String>(dupeList));
			connStr = "";
			for (String name : dedupeList) {
				ingredNameList += connStr + name;
				connStr = connector;
			}			
		}
		
		//"ingredSetName": "For|the|cake|For the cake|For|the|icing|For the icing|",
		//"instructSetName": "For|the|cake|For the cakeFor|the|icing|For the icing",

		if (instructCount > 1) {
			String names = "";
			String connector = "|";
			String connStr = "";
			/*for (InstructionSection sect : recipe.getInstructSections()) {
				String name = StringUtils.replaceChars(sect.getName(), ' ', '|');
				instructNameList = instructNameList + name + "|" + sect.getName();				
			}*/			 
			for (InstructionSection sect : recipe.getInstructSections()) {
				String[] words = StringUtils.split(sect.getName());
				if (words.length > 1) {
					for (int i=0;i<words.length;i++) {
						names += connStr + words[i];
						connStr = connector;
					}
					if (words.length > 2) {
						for (int i=0;i<(words.length-1);i++) {
							names += connStr + words[i] + " " + words[i+1];
						}
					}
					names += connStr + sect.getName(); 
				}
				else
					names = sect.getName();
			}
			String[] nameArray = StringUtils.split(names, "|");
			List<String> dupeList = Arrays.asList(nameArray);
			List<String> dedupeList = new ArrayList<String>(new LinkedHashSet<String>(dupeList));
			connStr = "";
			for (String name : dedupeList) {
				instructNameList += connStr + name;
				connStr = connector;
			}			
		}
		
		contextMap.put("ingredSetName", ingredNameList);
		contextMap.put("instructSetName", instructNameList);
		return contextMap;
	}
	
	private boolean translateWatsonCommand(ConversationType converseType, List<Entity> entities, Recipe recipe, User user, Voice voice, 
			HttpServletResponse response, Locale locale) {
		String watsonText = "";
		String fileName = "";
		AudioType audioType = null;
		boolean result = false;
		
		switch (converseType)
		{
		case NOTTODAY				:
		case WHICHPART				:
		case ELEMENT_NA				:
		case ELEMENT_BAD			:
		case NOTUNDERSTAND			:	
		case INGREDSET_BAD			:
		case INSTRUCTSET_BAD		:
		case INGREDSET_TRYAGAIN		:
		case INSTRUCTSET_TRYAGAIN	:
			watsonText = messages.getMessage("watson." + StringUtils.lowerCase(converseType.name()), null, "Error", locale);
			fileName = speechUtil.getSpeechDir() + "conversation." + StringUtils.lowerCase(converseType.name()) + "." + voice.getName() + ".ogg";
			result = speechUtil.getAudio(watsonText, voice, fileName, true, response);
			break;
		case GREETING				:
			break;
		case ELEMENT_PLAY			:
			audioType = getAudioType(entities);
			if (audioType != null) {
				result = getRecipeElement(voice, recipe, user, 0, audioType, response, locale);
			}
			break;			
		case INGREDSET				:
		case INSTRUCTSET			:
			audioType = getSetType(converseType);
			Object[] obj = new Object[] {null};
			obj[0] = getSetNames(recipe, converseType);
			watsonText = messages.getMessage("watson." + StringUtils.lowerCase(converseType.name()), obj, "Error", locale);
			fileName = speechUtil.getSpeechDir() + "recipe" + recipe.getId() + "." + StringUtils.lowerCase(converseType.name()) + "." + voice.getName() + ".ogg";
			result = speechUtil.getAudio(watsonText, voice, fileName, true, response);
			break;
		case INGREDSET_MATCH		:
		case INSTRUCTSET_MATCH		:
			break;
		case PAUSE					:
		case CONTINUE				:
		case REPLAY					:
		case STOP					:	
			watsonText = "Error";
			result = speechUtil.getAudio(watsonText, voice, fileName, false, response);
			break;
		}
		
		if (!result) {
			//TODO: SPEECH: return saved sorry audio and error
			//speechUtil.getNoAudioFile(AudioType.INGREDIENTS, defaultVoiceEN, response);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
		
		return result;
	}

	private AudioType getAudioType(List<Entity> entities) {
		AudioType type = null;
		
		if (entities.size() == 1) {
			String value = entities.get(0).getValue();
			//TODO: SPEECH: can this be simplified?
			if (StringUtils.equalsIgnoreCase(value, "ingredient"))
				type = AudioType.INGREDIENTS;
			if (StringUtils.equalsIgnoreCase(value, "instruction"))
				type = AudioType.INSTRUCTIONS;
			if (StringUtils.equalsIgnoreCase(value, "note"))
				type = AudioType.NOTES;
			if (StringUtils.equalsIgnoreCase(value, "private"))
				type = AudioType.PRIVATENOTES;
		}
		else {
			boolean foundPrivate = false;
			boolean foundNote = false;
			for (Entity entity : entities) {
				String value = entity.getValue();
				if (StringUtils.equalsIgnoreCase(value, "private"))
					foundPrivate = true;
				if (StringUtils.equalsIgnoreCase(value, "note"))
					foundNote = true;
			}
			if (foundPrivate)
				type = AudioType.PRIVATENOTES;
			if (!foundPrivate && foundNote)
				type = AudioType.NOTES;
		}
		
		return type;
	}

	private AudioType getSetType(ConversationType converseType) {
		AudioType type = null;

		if (converseType == ConversationType.INGREDSET)
			type = AudioType.INGREDIENTS;
		if (converseType == ConversationType.INSTRUCTSET)
			type = AudioType.INSTRUCTIONS;
		
		return type;
	}
	
	private String getSetNames(Recipe recipe, ConversationType converseType) {
		String names = "";
		String connector = " or ";
		String connStr = "";
		
		if (converseType == ConversationType.INGREDSET) {
			for (IngredientSection sect : recipe.getIngredSections()) {
				String name = sect.getName();
				names += connStr + name;
				connStr = connector;
			}
		}

		connStr = "";		
		if (converseType == ConversationType.INSTRUCTSET) {
			for (InstructionSection sect : recipe.getInstructSections()) {
				String name = sect.getName();
				names += connStr + name;
				connStr = connector;
			}
		}
		
		return names;
	}
}

/*
{
	  "context": {
	    "system": {
	      "dialog_stack": [
	        "node_7_1474815246286"
	      ],
	      "dialog_turn_counter": 4.0,
	      "dialog_request_counter": 4.0
	    },
	    "instructSetName": "For|the|cake|For the cakeFor|the|icing|For the icing",
	    "ingredSetCount": 2.0,
	    "conversation_id": "c0ba648d-9be1-42b8-95b1-6f6a0a3a73a5",
	    "instructSetCount": 2.0,
	    "ingredSetName": "For|the|cake|For the cake|For|the|icing|For the icing|",
	    "counter": 0.0
	  },
	  "entities": [
	    {
	      "entity": "recipeElement",
	      "location": [
	        0,
	        11
	      ],
	      "value": "ingredient"
	    }
	  ],
	  "intents": [
	    {
	      "confidence": 1.0,
	      "intent": "play_element"
	    }
	  ],
	  "output": {
	    "log_messages": [],
	    "text": [
	      "INGREDSET"
	    ],
	    "nodes_visited": [
	      "node_1_2",
	      "node_7_1474815246286"
	    ]
	  },
	  "input": {
	    "text": "ingredients "
	  }
	}*/


/*
	"context": {
		"ingredSetCount": 2.0,
		"instructSetCount": 2.0,	
		"instructSetName": "For|the|cake|For the|the cake|For the cake|icing|the icing|For the icing",
		"ingredSetName": "For|the|cake|For the|the cake|For the cake|icing|the icing|For the icing"
	}
{
	  "context": {
	    "system": {
	      "dialog_stack": [
	        "root"
	      ],
	      "dialog_turn_counter": 7.0,
	      "dialog_request_counter": 7.0
	    },
	    "instructSetName": "For|the|cake|For the cakeFor|the|icing|For the icing",
	    "ingredSetCount": 2.0,
	    "conversation_id": "c0ba648d-9be1-42b8-95b1-6f6a0a3a73a5",
	    "counter": 0.0,
	    "instructSetCount": 2.0,
	    "ingredSetName": "For|the|cake|For the cake|For|the|icing|For the icing|"
	  },
	  "entities": [
	    {
	      "entity": "recipeElement",
	      "location": [
	        13,
	        26
	      ],
	      "value": "private"
	    }
	  ],
	  "intents": [
	    {
	      "confidence": 1.0,
	      "intent": "play_element"
	    }
	  ],
	  "output": {
	    "log_messages": [],
	    "text": [
	      "INGREDSET_TRYAGAIN",
	      "WHICHPART"
	    ],
	    "nodes_visited": [
	      "node_2_1474898703004",
	      "node_4_1474899352569"
	    ]
	  },
	  "input": {
	    "text": "and like the private notes "
	  }
	}*/
/*
	{
	  "context": {
	    "system": {
	      "dialog_stack": [
	        "node_7_1474815246286"
	      ],
	      "dialog_turn_counter": 16.0,
	      "dialog_request_counter": 16.0
	    },
	    "instructSetName": "For|the|cake|For the|the cake|For the cake|icing|the icing|For the icing",
	    "ingredSetCount": 2.0,
	    "conversation_id": "91ff5122-2979-4405-9954-bb63b0a2daf2",
	    "counter": 1.0,
	    "instructSetCount": 2.0,
	    "ingredSetName": "For|the|cake|For the|the cake|For the cake|icing|the icing|For the icing"
	  },
	  "entities": [],
	  "intents": [
	    {
	      "confidence": 1.0,
	      "intent": "play_element"
	    }
	  ],
	  "output": {
	    "log_messages": [],
	    "text": [
	      "INGREDSET_BAD",
	      "INGREDSET"
	    ],
	    "nodes_visited": [
	      "node_1_1474898622896",
	      "node_7_1474815246286"
	    ]
	  },
	  "input": {
	    "text": "the icing "
	  }
	}*/