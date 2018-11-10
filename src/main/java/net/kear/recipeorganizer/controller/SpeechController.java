package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.exception.WatsonUnavailableException;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
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
import com.ibm.watson.developer_cloud.assistant.v1.model.Context;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v1.model.OutputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.RuntimeEntity;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.KeywordResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionAlternative;
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
	@Autowired
	private ExceptionLogService logService;
	
	private static final String defaultVoiceEN = "en-US_AllisonVoice";
	private static final String defaultVoiceFR = "fr-FR_ReneeVoice";
	
	private static final Map<String, Integer> setNumberMap;
	static {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("one",0);
		map.put("two",1);
		map.put("three",2);
		setNumberMap = Collections.unmodifiableMap(map);
	}
	private static final String[] commonWords = {"a","an","the","this","that","these","those","of","in","to","for","with","on","at","from","by","from","as","like","into"};
	private static final List<String> commonList = new ArrayList<String>(Arrays.asList(commonWords));
	private static final Gson GSON = GsonSingleton.getGsonWithoutPrettyPrinting();

	//this method is used for users who use the VCR buttons to play sections of the recipe
	@RequestMapping(value = "/getRecipeAudio", method = RequestMethod.GET)
	public void getRecipeAudio(@RequestParam("userId") final Long userId, @RequestParam("recipeId") final Long recipeId, 
			@RequestParam("section") final Integer section, @RequestParam("type") final AudioType type, HttpServletResponse response, Locale locale) {
		logger.debug("getRecipeAudio:requestparam");

		Voice voice = speechUtil.getVoice(defaultVoiceEN); 
		if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
			voice = speechUtil.getVoice(defaultVoiceFR);
		
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
				if (userProfile.getTtsVoice() != null) {
					voice = speechUtil.getVoice(userProfile.getTtsVoice());
				}				
			}
		}		
	
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			logService.addException(ex);
			speechUtil.getNoAudio(type, voice, response);
			return;
		}
		
		boolean result = getRecipeElement(voice, recipe, user, section, type, response, locale);		
		if (!result) {
			speechUtil.getNoAudio(type, voice, response);
		}
	}

	@RequestMapping(value = "/getSample", method = RequestMethod.GET)
	public void getSample(@RequestParam("voiceName") final String voiceName, HttpServletResponse response, Locale locale) {
		logger.debug("getSample");
		
		String fileName = speechUtil.getSpeechDir() + "sample." + voiceName + ".ogg";
		if (!speechUtil.getSample(fileName, response)) {
			Voice voice = speechUtil.getVoice(voiceName);
			speechUtil.watsonUnavailable(voice, response);
		}
	}
	
	@RequestMapping(value = "/getWatsonToken", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public String getWatsonToken() throws RestException {
		logger.info("getWatsonToken");
	
		if (!speechUtil.isWatsonAsstAvailable() || !speechUtil.isWatsonSTTAvailable()) {
			throw new RestException("exception.watsonUnavailable", new WatsonUnavailableException());
		}
		
		String token = "";
		try {
			token = speechUtil.getSTTToken();
		}
		catch (Exception ex) {
			throw new RestException("exception.watsonUnavailable", ex);
		}
		
		if (StringUtils.isEmpty(token))
			throw new RestException("exception.watsonUnavailable", new WatsonUnavailableException());
		
		return token;
	}

	@RequestMapping(value = "/getWatsonKeywords", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<String> getWatsonKeywords(@RequestParam("recipeId") Long recipeId, Locale locale) throws RestException {
		logger.info("getWatsonKeywords GET");

		List<String> keywords = Arrays.asList(speechUtil.getKeywords());
		
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			throw new RestException("exception.watsonUnavailable", new WatsonUnavailableException(ex));
		}

		int ingredCount = recipe.getIngredSections().size();
		int instructCount = recipe.getInstructSections().size();
		List<String> tempList = new ArrayList<String>();
		
		if (ingredCount > 1) {
			for (IngredientSection sect : recipe.getIngredSections()) {
				String[] words = StringUtils.split(sect.getName().toLowerCase());
				List<String> wordList = Arrays.asList(words);
				tempList.addAll(wordList);
			}
		}
		
		if (instructCount > 1) {
			for (InstructionSection sect : recipe.getInstructSections()) {
				String[] words = StringUtils.split(sect.getName().toLowerCase());
				List<String> wordList = Arrays.asList(words);
				tempList.addAll(wordList);
			}
		}
		
		if (!tempList.isEmpty()) {
			ArrayList<String> tempKeywords = new ArrayList<String>(keywords);
			ArrayList<String> dedupeList = new ArrayList<String>(new LinkedHashSet<String>(tempList));
			tempKeywords.addAll(dedupeList);
			keywords = tempKeywords;
		}
				
		return keywords;
	}

	//Note: several things were needed to get this to work:
	//	the Ajax call must not include contentType: 'application/json; charset=utf-8'
	//	the result object from Watson must be wrapped in an outer Json layer, e.g., {message:data}
	//		(data = stringified result Json object from Watson)
	//	do not use @RequestBody
	//	the Watson result must be captured by the 'receive-json' event, not the 'data' event
	//Note: the first call will not have a message (="") so that Watson will return the initial greeting
	//@SuppressWarnings("unchecked")
	@RequestMapping(value = "/postWatsonResult", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public void postWatsonResult(@RequestParam("userId") Long userId, @RequestParam("recipeId") Long recipeId, @RequestParam("message") String message, 
			HttpServletResponse response, HttpSession session, Locale locale) throws RestException {
		logger.info("postWatsonResult");

		//set the status manually so that a 500 status can be passed to the client in the event of an error;
		//this allows the javascript routine to turn off the microphone
		response.setStatus(HttpServletResponse.SC_OK);
		
		Voice voice = (Voice)session.getAttribute("watsonVoice");
		if (voice == null) {
			voice = speechUtil.getVoice(defaultVoiceEN);
			if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
				voice = speechUtil.getVoice(defaultVoiceFR);
		}
		
		User user = null;
		try {
			user = userService.getUser(userId);
		} 
		catch (Exception ex) {
			logService.addException(ex);
		}
		
		Recipe recipe = null;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			logService.addException(ex);
		}

		//getUser() returns null if the object is not found in the db; no exception is thrown until you try to use the object
		//getRecipe() will throw an exception since it does more than just return the recipe object
		//TODO: watsonUnavailable is not returning an audio file correctly to be played
		if ((user == null) || (recipe == null) || !speechUtil.isWatsonAsstAvailable() || !speechUtil.isWatsonSTTAvailable()) {
			speechUtil.watsonUnavailable(voice, response);			
			return;
		}

		//Note: this code is an alternative to returning the unavailable audio message and is here as an example for handling XMLHttpRequest() calls;
		//it's not possible to throw a RestException for this method - that method produces a ResponseObject which does not automatically
		//get transformed into JSON because this method is missing @ResponseBody and Spring doesn't know what to do with the ResponseObject;
		//the javascript method is expecting a Blob if the method returns 200 and a blob or JSON for any other return value;
		//to return JSON add MediaType.APPLICATION_JSON_VALUE to the @RequestMapping produces list; 
		/*if (user == null) {
			ObjectMapper mapper = new ObjectMapper();
			ResponseObject obj = new ResponseObject("Watson unavailable", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			try {
				mapper.writeValue(response.getOutputStream(), obj);
			} catch (Exception ex) {
				logService.addException(ex);
			}

			return;
		}*/

		//following code lifted from Watson Java SDK WebSocketManager
		JsonObject json = new JsonParser().parse(message).getAsJsonObject();
		SpeechRecognitionResults results = null;
		if (json.has("results")) {
			results = GSON.fromJson(message, SpeechRecognitionResults.class);
			if (results == null) {
				speechUtil.watsonUnavailable(voice, response);
				return;
			}
		}
		//indicates the page has been refreshed, the user is starting over or it's a different recipe
		//so remove any prior session attributes
		if (json.has("start")) {
			session.removeAttribute("watsonContext");
			session.removeAttribute("watsonVoice");
			session.removeAttribute("watsonCommand");
		}

		Context context = (Context)session.getAttribute("watsonContext");
		//HashMap<String, Object> context = (HashMap<String, Object>)session.getAttribute("watsonContext");
		//the context will be null for the initial call
		if (context == null) {
			//get the recipe info for the conversation context map
			context = setContextMap(recipe);
		}

		//default to no text which prompts the greeting
		String speechText = "";
		if (results != null) {
			String keywordText = "";
			speechText = results.getResults().get(0).getAlternatives().get(0).getTranscript();
			logger.debug("transcript text: " + speechText);
			ConversationType priorCommand = (ConversationType)session.getAttribute("watsonCommand");
			if (priorCommand != null) {
				//for recipes with multiple sets of ingredients or instructions, Watson Conversation attempts to match on a
				//few keywords from the set names loaded into the context in the initial call; Watson has difficulty understanding 
				//just one or two words (not enough context) so the user is encouraged to speak in sentences; however, this
				//makes it difficult to match on the set name keywords, so parsing the keyword results and alternatives is
				//a way to pass to Watson Conversation a condensed version of what was said; note that Watson Conversation is
				//also capable of matching on a set number through an @Entity, e.g., "read set number one"; it is assumed that
				//the user will not speak both a set number and a set name, so the user's full text in this case will be passed
				//along and a match found
				if ((priorCommand == ConversationType.INGREDSET) 		||
					(priorCommand == ConversationType.INSTRUCTSET)		||
					(priorCommand == ConversationType.INGREDSET_BAD)	||
					(priorCommand == ConversationType.INSTRUCTSET_BAD)) {
					SpeechRecognitionResult result = results.getResults().get(0);
					keywordText = parseKeywordResults(context, priorCommand, result.getKeywordsResult(), result.getAlternatives());
					logger.debug("keywordText: " + keywordText);
				}
			}
	
			if (!keywordText.isEmpty())
				speechText = keywordText;
			logger.debug("final text: " + speechText);
			speechText = speechText.toLowerCase().trim();
		}
		
		//send what the user said to Watson Conversation
		MessageResponse resp = null;
		try {
			InputData input = new InputData.Builder(speechText).build();
			MessageRequest req = new MessageRequest();
			req.setInput(input);
			req.setContext(context);
			resp = speechUtil.sendWatsonRequest(req);
		} catch (Exception ex) {
			logService.addException(ex);
			speechUtil.watsonUnavailable(voice, response);
			return;
		}
		
		if (resp == null) {
			speechUtil.watsonUnavailable(voice, response);
			return;
		}
			
		//save off the most recent context for the next time
		session.setAttribute("watsonContext", resp.getContext());
		
		//Watson Conversation returns a "command" (see ConversationType enum) instead of a phrase to be read by Watson TTS
		String command = null;
		if (resp.getOutput() != null) {
			OutputData data = resp.getOutput();
			List<String> text = data.getText();
			if (!text.isEmpty())
				command = text.get(0); 			
		}
		logger.debug("command: " + command);
		
		if (StringUtils.isBlank(command)) {
			speechUtil.watsonUnavailable(voice, response);
			return;
		}
				
		voice = (Voice)session.getAttribute("watsonVoice");
		//the voice will be null for the initial call
		if (voice == null) {
			voice = speechUtil.getVoice(defaultVoiceEN);
			if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
				voice = speechUtil.getVoice(defaultVoiceFR);
			if (user != null) {
				UserProfile userProfile = user.getUserProfile();
				if (userProfile != null) {
					if (userProfile.getTtsVoice() != null)
						voice = speechUtil.getVoice(userProfile.getTtsVoice());
				}
			}

			//check to make sure the selected voice matches the recipe language
			String voiceLang = voice.getLanguage().substring(0, 2);
			if (!StringUtils.equals(voiceLang, recipe.getLang())) {
				if (recipe.getLang().equals(new Locale("fr").getLanguage()))
					voice = speechUtil.getVoice(defaultVoiceFR);
				if (recipe.getLang().equals(new Locale("en").getLanguage())) {
					voice = speechUtil.getVoice(defaultVoiceEN);
				}
			}
			
			session.setAttribute("watsonVoice", voice);
		}		
		
		ConversationType type = ConversationType.valueOf(command);
		//save off the current command
		session.setAttribute("watsonCommand", type);
		
		List<RuntimeEntity> entities = resp.getEntities();
		//turn the command into an audio file
		boolean result = translateWatsonCommand(type, entities, speechText, recipe, user, voice, response, locale);
		if (!result) {
			speechUtil.watsonUnavailable(voice, response);
			return;
		}
	}
	
	private boolean getRecipeElement(Voice voice, Recipe recipe, User user, int section, AudioType type, HttpServletResponse response, Locale locale) {
		String watsonText = "";
		String extra = "";
		long userId = user.getId();
		long recipeId = recipe.getId();
		
		String voiceLang = voice.getLanguage().substring(0, 2);
		if (!StringUtils.equals(voiceLang, recipe.getLang())) {
			voice = speechUtil.getVoice(defaultVoiceEN);
			if (locale.getLanguage().equals(new Locale("fr").getLanguage()))
				voice = speechUtil.getVoice(defaultVoiceFR);
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

		String fileName = "";
		if ((type == AudioType.NOTES)	||
			(type == AudioType.PRIVATENOTES)) {
			if (watsonText.isEmpty()) {
				watsonText = messages.getMessage("watson." + StringUtils.lowerCase(type.name()) + ".notavailable", null, null, locale);
				fileName = speechUtil.getSpeechDir() + "notavailable." + StringUtils.lowerCase(type.name()) + "." + voice.getName() + ".ogg";
				return (speechUtil.getAudio(fileName, watsonText, voice, true, response));
			}
		}

		fileName = speechUtil.getSpeechDir() + "recipe" + recipeId + "." + StringUtils.lowerCase(type.name()) + extra + "." + voice.getName() + ".ogg";
		return (speechUtil.getRecipeAudio(fileName, watsonText, voice, recipeDateTime, response));
	}	
	
	private String getIngredText(Recipe recipe, int section) {
		IngredientSection ingredSection = recipe.getIngredSections().get(section);
		List<RecipeIngredient> ingreds = ingredSection.getRecipeIngredients();
		String text = speechUtil.prepareIngredients(ingreds, 0);
		logger.debug("watson ingredient text: " + text);
		return text;
	}
	
	private String getInstructText(Recipe recipe, int section) {
		InstructionSection instructSection = recipe.getInstructSections().get(section);
		List<Instruction> instructs = instructSection.getInstructions();
		String text = speechUtil.prepareInstructions(instructs, 0);
		logger.debug("watson instruction text: " + text);
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
		String text = "";
		if (!notes.isEmpty())
			text = speechUtil.prepareNotes(notes, 0);
		logger.debug("watson note/private text: " + text);
		return text;
	}
	
	private Context setContextMap(Recipe recipe) {
		//for multiple sets of ingredients or instructions the context must indicate how many sets
		//and a regex of words from the set name must be constructed with common words removed plus
		//the entire set name; for example the set names "for the cake" and "for the icing" will return
		//"cake|for the cake|icing|for the icing"; Watson Conversation will try to exactly match one of
		//the regex elements
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
			for (IngredientSection sect : recipe.getIngredSections()) {
				String sectionName = sect.getName().toLowerCase();
				names += connStr + parseSectionName(sectionName);
				connStr = connector;
			}
			names = names.trim();
			//convert the name string into an array			
			String[] nameArray = StringUtils.split(names, "|");
			//convert the array into an ArrayList, then a LinkedHashSet which can be deduped			
			List<String> dupeList = Arrays.asList(nameArray);
			List<String> dedupeList = new ArrayList<String>(new LinkedHashSet<String>(dupeList));
			//convert the result back into a string			
			ingredNameList = StringUtils.join(dedupeList, "|");
		}
		
		if (instructCount > 1) {
			String names = "";
			String connector = "|";
			String connStr = "";
			for (InstructionSection sect : recipe.getInstructSections()) {
				String sectionName = sect.getName().toLowerCase();
				names += connStr + parseSectionName(sectionName);
				connStr = connector;
			}
			names = names.trim();
			//convert the name string into an array 
			String[] nameArray = StringUtils.split(names, "|");
			//convert the array into an ArrayList, then a LinkedHashSet which can be deduped
			List<String> dupeList = Arrays.asList(nameArray);
			List<String> dedupeList = new ArrayList<String>(new LinkedHashSet<String>(dupeList));
			//convert the result back into a string
			instructNameList = StringUtils.join(dedupeList, "|");
		}
		
		contextMap.put("ingredSetName", ingredNameList);
		contextMap.put("instructSetName", instructNameList);
		
		Context context = new Context();
		//context = (Context)contextMap;
		context.putAll(contextMap);
		return context;
	}

	private String parseSectionName(String sectionName) {
		String names = "";
		String connector = "|";
		String connStr = "";
		String[] words = StringUtils.split(sectionName);
		//convert the array to an ArrayList and remove any common words (must be an ArrayList - List.removeall throws an exception				
		ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(words));				
		wordList.removeAll(commonList);
		//nothing left (unlikely) just add the name to the string
		if (wordList.isEmpty()) {
			names += connStr + sectionName;
			connStr = connector;
		}
		else {
			if (wordList.size() > 1) {
				int len = wordList.size();
				//the nested for loops allow for n number of words in the array
				//the result should be each word separated by "|", each combination of 2 contiguous words separated by "|", each combination of 3 words...
				for (int i=0;i<len-1;i++) {
					for (int j=0;j<(len-i);j++) {
						names += connStr + (i > 0 ? wordList.get(j) + " " : "") + wordList.get(j+i);
						connStr = connector;
					}
				}
				//add the full name just in case that is matched						
				names += connStr + StringUtils.join(wordList, " ");
			}
			else {
				names += connStr + wordList.get(0);
				connStr = connector;
			}
		}
		names += connStr + sectionName;
		names = names.trim();
		return names;
	}
	
	private boolean translateWatsonCommand(ConversationType converseType, List<RuntimeEntity> entities, String spokenText, Recipe recipe, 
			User user, Voice voice, HttpServletResponse response, Locale locale) {
		String watsonText = "";
		String fileName = "";
		AudioType audioType = null;
		boolean result = false;
		Object[] obj = new Object[] {null,null};
		
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
			result = speechUtil.getAudio(fileName, watsonText, voice, true, response);
			break;
		case GREETING				:
			obj[0] = user.getFirstName();
			obj[1] = recipe.getName();
			watsonText = messages.getMessage("watson.greeting", obj, "Hello", locale);
			result = speechUtil.getAudio(fileName, watsonText, voice, false, response);
			break;
		case ELEMENT_PLAY			:
			audioType = getAudioType(entities);
			if (audioType != null) {
				result = getRecipeElement(voice, recipe, user, 0, audioType, response, locale);
			}
			else {
				ConversationType type = ConversationType.ELEMENT_BAD;
				watsonText = messages.getMessage("watson." + StringUtils.lowerCase(type.name()), null, "Error", locale);
				fileName = speechUtil.getSpeechDir() + "conversation." + StringUtils.lowerCase(type.name()) + "." + voice.getName() + ".ogg";
				result = speechUtil.getAudio(fileName, watsonText, voice, true, response);
			}
			break;			
		case INGREDSET				:
		case INSTRUCTSET			:
			audioType = converseType == ConversationType.INGREDSET ? AudioType.INGREDIENTS : AudioType.INSTRUCTIONS;
			obj[0] = getSetNames(recipe, converseType);
			watsonText = messages.getMessage("watson." + StringUtils.lowerCase(converseType.name()), obj, "Error", locale);
			fileName = speechUtil.getSpeechDir() + "recipe" + recipe.getId() + "." + StringUtils.lowerCase(converseType.name()) + "." + voice.getName() + ".ogg";
			result = speechUtil.getAudio(fileName, watsonText, voice, true, response);
			break;
		case INGREDSET_MATCH		:
		case INSTRUCTSET_MATCH		:
			audioType = converseType == ConversationType.INGREDSET_MATCH ? AudioType.INGREDIENTS : AudioType.INSTRUCTIONS;
			int setNdx = getSetIndex(entities, spokenText, audioType, recipe);
			result = getRecipeElement(voice, recipe, user, setNdx, audioType, response, locale);
			break;
		}
		
		return result;
	}

	private AudioType getAudioType(List<RuntimeEntity> entities) {
		AudioType type = null;
		
		if (entities.size() == 1) {
			String value = entities.get(0).getValue();
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
			//private notes may return two entity values
			boolean foundPrivate = false;
			boolean foundNote = false;
			for (RuntimeEntity entity : entities) {
				String value = entity.getValue();
				if (StringUtils.equalsIgnoreCase(value, "private")) {
					foundPrivate = true;
					break;
				}
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

	private int getSetIndex(List<RuntimeEntity> entities, String watsonText, AudioType audioType, Recipe recipe) {
		//check for an entity match and return the appropriate index
		//for example, the user says "second" and the returned entity will be "two" 
		for (RuntimeEntity entity : entities) {
			String ent = entity.getValue();
			if (setNumberMap.containsKey(ent))
				return setNumberMap.get(ent); 
		}
		
		//TODO: SPEECH: improve this method of determining the section
		//check the text returned from Watson against the set names and return the appropriate index
		if (audioType == AudioType.INGREDIENTS) {
			int highCount = 0;
			int setNdx = 0;
			for (IngredientSection sect : recipe.getIngredSections()) {
				String sectionName = sect.getName().toLowerCase();
				String regex = ".*\\b" + sectionName + "\\b.*";
				String inputText = watsonText.toLowerCase();
				//try the whole phrase first (it's worth a shot)
				if (inputText.matches(regex))
					return sect.getSequenceNo()-1;
				//count how many times a word in the section name if found in the Watson text
				//this is an imperfect method that needs improvement
				String[] setWords = StringUtils.split(sectionName);
				String[] inputWords = StringUtils.split(inputText);
				int count = 0;
				for (int i=0;i<setWords.length;i++) {
					String matchWord = setWords[i];
					for (int j=0;j<inputWords.length;j++) {
						if (StringUtils.equals(inputWords[j], matchWord))
							count++;
					}
				}
				if (count > highCount) {
					highCount = count;
					setNdx = sect.getSequenceNo()-1;
				}
			}
			return setNdx;
		}

		if (audioType == AudioType.INSTRUCTIONS) {
			int highCount = 0;
			int setNdx = 0;
			for (InstructionSection sect : recipe.getInstructSections()) {
				String sectionName = sect.getName().toLowerCase();
				String regex = ".*\\b" + sectionName + "\\b.*";
				String inputText = watsonText.toLowerCase();
				//try the whole phrase first (it's worth a shot)
				if (inputText.matches(regex))
					return sect.getSequenceNo()-1;
				//count how many times a word in the section name if found in the Watson text
				//this is an imperfect method that needs improvement
				String[] setWords = StringUtils.split(sectionName);
				String[] inputWords = StringUtils.split(inputText);
				int count = 0;
				for (int i=0;i<setWords.length;i++) {
					String matchWord = setWords[i];
					for (int j=0;j<inputWords.length;j++) {
						if (StringUtils.equals(inputWords[j], matchWord))
							count++;
					}
				}
				if (count > highCount) {
					highCount = count;
					setNdx = sect.getSequenceNo()-1;
				}
			}
			return setNdx;
		}
		
		//default to the first set
		return 0;
	}
	
	private String getSetNames(Recipe recipe, ConversationType converseType) {
		String names = "";
		String connector = ", or ";
		String setNum = " set number ";
		String connStr = "";
		
		if (converseType == ConversationType.INGREDSET) {
			for (IngredientSection sect : recipe.getIngredSections()) {
				String name = setNum + sect.getSequenceNo() + ", " + sect.getName();
				names += connStr + name;
				connStr = connector;
			}
		}

		connStr = "";		
		if (converseType == ConversationType.INSTRUCTSET) {
			for (InstructionSection sect : recipe.getInstructSections()) {
				String name = setNum + sect.getSequenceNo() + ", " + sect.getName();
				names += connStr + name;
				connStr = connector;
			}
		}
		
		return names;
	}
	
	private String parseKeywordResults(Map<String, Object> context, ConversationType priorCommand, Map<String, List<KeywordResult>> keywordMap, 
			List<SpeechRecognitionAlternative> alternatives) {
		String text = "";
		String normalText = "";
		double confidence = 0;
		double startTime = 0;
		
		KeywordResultComparator keywordComparator = new KeywordResultComparator();		
		SpeechRecognitionAlternativeComparator speechComparator = new SpeechRecognitionAlternativeComparator(); 

		List<KeywordResult> keywordList = new ArrayList<KeywordResult>();
		//add all of the KeywordsResult objects to a single array
		for (Map.Entry<String, List<KeywordResult>> resultObj : keywordMap.entrySet()) {
			List<KeywordResult> resultList = resultObj.getValue();
			keywordList.addAll(resultList);
		}
		//sort the array
		Collections.sort(keywordList, keywordComparator);
		//piece together a phrase
		for (KeywordResult result : keywordList) {
			normalText = result.getNormalizedText();
			confidence = result.getConfidence();
			startTime = result.getStartTime();
			logger.debug("keyWord normalText: " + normalText + " confidence: " + confidence + " startTime: " + startTime);
			text += normalText + " ";
		}
		text = text.trim();
		
		String setNameList = "";
		//get the set name regex from the context
		if ((priorCommand == ConversationType.INGREDSET) ||
			(priorCommand == ConversationType.INGREDSET_BAD))				
			setNameList = (String)context.get("ingredSetName");
		else
			setNameList = (String)context.get("instructSetName");
		
		if (text.matches(setNameList))
			return text;
		
		//remove the common words
		String[] words = StringUtils.split(text);
		ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(words));				
		wordList.removeAll(commonList);
		text = StringUtils.join(wordList, "|");
		
		if (text.matches(setNameList))
			return text;

		//try the alternative words
		List<SpeechRecognitionAlternative> speechList = new ArrayList<SpeechRecognitionAlternative>();
		//sort the array
		Collections.sort(speechList, speechComparator);
		for (SpeechRecognitionAlternative alternative : speechList) {
			normalText = alternative.getTranscript();
			confidence = alternative.getConfidence();
			//there may be multiple occurrences of the word - sort on the first one
			startTime = alternative.getTimestamps().get(0).getStartTime();
			logger.debug("keyWord normalText: " + normalText + " confidence: " + confidence + " startTime: " + startTime);
			text += normalText + " ";
		}
		text = text.trim();
		
		if (text.matches(setNameList))
			return text;

		//remove the common words
		words = StringUtils.split(text);
		wordList = new ArrayList<String>(Arrays.asList(words));				
		wordList.removeAll(commonList);
		text = StringUtils.join(wordList, "|");
		
		if (text.matches(setNameList))
			return text;

		return text;
	}
	
	private static class KeywordResultComparator implements Comparator<KeywordResult> {
		@Override
		public int compare(KeywordResult kr1, KeywordResult kr2) {
			return compare(kr1.getStartTime(), kr2.getStartTime());
		}
		
		private int compare(double a, double b) {
			return a < b ? -1
		         : a > b ? 1
		         : 0;
		}
	}

	private static class SpeechRecognitionAlternativeComparator implements Comparator<SpeechRecognitionAlternative> {
		@Override
		public int compare(SpeechRecognitionAlternative sa1, SpeechRecognitionAlternative sa2) {
			return compare(sa1.getTimestamps().get(0).getStartTime(), sa2.getTimestamps().get(0).getStartTime());
		}
		
		private int compare(double a, double b) {
			return a < b ? -1
		         : a > b ? 1
		         : 0;
		}
	}
}

/*
"context":{
	"ingredSetName":"for|the|cake|icing",
	"ingredSetCount":"2"
	}
*/