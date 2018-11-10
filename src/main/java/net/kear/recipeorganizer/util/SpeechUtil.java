package net.kear.recipeorganizer.util;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.ibm.watson.developer_cloud.assistant.v1.model.Context;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;

public interface SpeechUtil {

	public void initWatson();
	public void setWatsonTTSAccount(String username, String password, String url);
	public void setWatsonSTTAccount(String username, String password, String url);
	public void setWatsonAsstAccount(String apiKey, String version, String url, String workspaceId);
	public void setSpeechDir(String dir);
	public String getSpeechDir();
	public String[] getKeywords();
	public boolean isWatsonTTSAvailable();
	public boolean isWatsonSTTAvailable();
	public boolean isWatsonAsstAvailable();
	public void watsonUnavailable(Voice voice, HttpServletResponse response);	
	public boolean getRecipeAudio(String fileName, String text, Voice voice, DateTime recipeDate, HttpServletResponse response);
	public boolean getAudio(String fileName, String text, Voice voice, boolean saveFile, HttpServletResponse response);
	public boolean getAudio(String name, HttpServletResponse response);
	public boolean getNoAudio(AudioType audioType, Voice voice, HttpServletResponse response);
	public boolean getSample(String fileName, HttpServletResponse response);
	public String prepareIngredients(List<RecipeIngredient> ingredList, int interval);
	public String prepareInstructions(List<Instruction> instructList, int interval);
	public String prepareNotes(String notes, int interval);
	public void getDefaultAudioFiles();
	public List<Voice> getVoices(Locale locale);
	public Voice getVoice(String name);
	public String getSTTToken();
	//public MessageResponse sendWatsonRequest(Context context, String speechText);
	public MessageResponse sendWatsonRequest(MessageRequest message);
}
