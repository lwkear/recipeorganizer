package net.kear.recipeorganizer.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;

public interface SpeechUtil {

	public void initWatson();
	public void setWatsonTTSAccount(String username, String password);
	public void setWatsonSTTAccount(String username, String password);
	public void setWatsonConvAccount(String username, String password, String workspaceId);
	public void setSpeechDir(String dir);
	public String getSpeechDir();
	public String[] getKeywords();
	public boolean isWatsonTTSAvailable();
	public boolean isWatsonSTTAvailable();
	public boolean isWatsonConvAvailable();
	public boolean getRecipeAudio(String fileName, String text, Voice voice, DateTime recipeDate, HttpServletResponse response);
	public boolean getAudio(String text, Voice voice, String fileName, boolean saveFile, HttpServletResponse response);
	public void getSample(String fileName, HttpServletResponse response);
	public String prepareIngredients(List<RecipeIngredient> ingredList, int interval);
	public String prepareInstructions(List<Instruction> instructList, int interval);
	public String prepareNotes(String notes, int interval);
	public void getDefaultAudioFiles();
	public void getNoAudioFile(AudioType audioType, Voice voice, HttpServletResponse response);
	public void getAudio(String name, HttpServletResponse response);
	public List<Voice> getVoices(Locale locale);
	public String getSTTToken();
	public MessageRequest startWatsonConversation(Map<String, Object> contextMap);
	public MessageResponse sendWatsonRequest(MessageRequest message);
}
