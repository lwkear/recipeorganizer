package net.kear.recipeorganizer.util;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;

public interface SpeechUtil {

	public void initTTS();
	public void setSpeechDir(String dir);
	public String getSpeechDir();
	public boolean isTtsAvailable();
	public void setWatsonTTSAccount(String username, String password);
	public boolean getAudio(String fileName, String text, Voice voice, DateTime recipeDate, HttpServletResponse response);
	public void getSample(String fileName, HttpServletResponse response);
	public String prepareIngredients(List<RecipeIngredient> ingredList, int interval);
	public String prepareInstructions(List<Instruction> instructList, int interval);
	public String prepareNotes(String notes, int interval);
	public void getNoAudioFiles();
	public void getNoAudioFile(AudioType audioType, Voice voice, HttpServletResponse response);
	public List<Voice> getVoices(Locale locale);
}
