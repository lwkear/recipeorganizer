package net.kear.recipeorganizer.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

public class SpeechUtilImpl implements SpeechUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;
	
	private final static String speakTagOpen = "<speak version='1.0'>";
	private final static String speakTagClose = "</speak>";
	private final static String breakTag = " <break time='%ds'/>";
	private final static int defaultInterval = 3;
	private final static AudioFormat audioFormat = AudioFormat.OGG_VORBIS;
	private String watsonTTSUsername = "";
	private String watsonTTSPassword = "";
	private boolean ttsInitialized = false;
	private boolean ttsAvailable = true;
	private TextToSpeech ttsService = null;
	private String speechDir = "";
	
	public SpeechUtilImpl() {}

	public void setWatsonTTSAccount(String username, String password) {
		this.watsonTTSUsername = username;
		this.watsonTTSPassword = password;
	}
	
	public void setSpeechDir(String dir) {
		this.speechDir = dir;
	}
	
	public String getSpeechDir() {
		return this.speechDir;
	}
	
	public void initTTS() {
		try {
			ttsService = new TextToSpeech(watsonTTSUsername, watsonTTSPassword);
		} catch (Exception ex) {
			logger.debug("TextToSpeech init(): " + ex.getMessage(), ex);
			logService.addException(ex);
			ttsAvailable = false;
		}
	}
	
	@EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
		//this routine gets called twice for the two contexts, hence the flag
		if (ttsInitialized)
			return;
		
		initTTS();
		getNoAudioFiles();
		ttsInitialized = true;
    }	
	
	public boolean isTtsAvailable() {
		return this.ttsAvailable;
	}
	
	public boolean getAudio(String fileName, String text, Voice voice, DateTime recipeDate, HttpServletResponse response) {
		InputStream inStream = null;
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;
		boolean result = false;
		
		if (StringUtils.isBlank(fileName) || StringUtils.isBlank(text) || voice == null)
			return result;
		
		//prepare the output file
		try {
			audioFile = new File(fileName);
			path = audioFile.toPath();
		} catch (Exception ex) {
	    	logService.addException(ex);
	    	return result;
		}
		
		//if the audio file already exists then simply return it in the response
		if (audioFile.exists()) {
    		long fileTime = audioFile.lastModified();
    		if (fileTime != 0) {
    			Date fileDate = new Date(fileTime);
    			DateTime fileDateTime = new DateTime(fileDate);
    			//send the existing audio since it was created after the recipe was last added/updated
    			if (recipeDate.isBefore(fileDateTime)) {
		    		try {
						outStream = response.getOutputStream();
				    	Files.copy(path, outStream);
				    	outStream.flush();
				    	outStream.close();
				    	return true;
				    } catch (Exception ex) {
				    	closeStream(outStream);
				    	logService.addException(ex);
				    	return result;
				    }
    			}
    		}
		}
		
		if (isTtsAvailable()) {
			//get audio from Watson then save it to a file and copy it to the response
			try {
				inStream = ttsService.synthesize(text, voice, audioFormat).execute();
				if (inStream != null) {
					FileUtils.copyInputStreamToFile(inStream, audioFile);
					outStream = response.getOutputStream();
			    	Files.copy(path, outStream);
			    	outStream.flush();
			    	result = true;
				}
			} catch (Exception ex) {
				logService.addException(ex);
			} finally {
			    closeStream(inStream);
			    closeStream(outStream);
			}
		}
		
		return result;
	}
	
	public void getSample(String fileName, HttpServletResponse response) {
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;
		
		if (StringUtils.isBlank(fileName))
			return;
		
		//prepare the output file
		try {
			audioFile = new File(fileName);
			path = audioFile.toPath();
		} catch (Exception ex) {
	    	logService.addException(ex);
	    	return;
		}
		
		//if the audio file already exists then simply return it in the response
		if (audioFile.exists()) {
			try {
				outStream = response.getOutputStream();
		    	Files.copy(path, outStream);
		    	outStream.flush();
		    	outStream.close();
		    	return;
		    } catch (Exception ex) {
		    	closeStream(outStream);
		    	logService.addException(ex);
		    	return;
		    }
		}
	}
	
	public String prepareIngredients(List<RecipeIngredient> ingredList, int interval) {
		String watsonText = speakTagOpen;
		String breakText = String.format(breakTag, interval > 0 ? interval : defaultInterval);
		String bTag = "";
		for (RecipeIngredient ingred : ingredList) {
			String ingredStr = ingred.getQuantity();
			if (StringUtils.isNotBlank(ingred.getQtyType()))
				ingredStr += " " + ingred.getQtyType();
			ingredStr += " " + ingred.getIngredient().getName();
			if (StringUtils.isNotBlank(ingred.getQualifier()))
				ingredStr += ". " + ingred.getQualifier();
			watsonText += bTag + ingredStr;
			bTag = breakText;
		}
		watsonText += speakTagClose;
		
		return watsonText;
	}
	
	public String prepareInstructions(List<Instruction> instructList, int interval) {
		String watsonText = speakTagOpen;
		String breakText = String.format(breakTag, interval > 0 ? interval : defaultInterval);
		String bTag = "";
		for (Instruction instruct : instructList) {
			String ingredStr = instruct.getDescription();
			watsonText += bTag + ingredStr;
			bTag = breakText;
		}
		watsonText += speakTagClose;
		
		return watsonText;
	}
	
	public String prepareNotes(String notes, int interval) {
		String watsonText = speakTagOpen + notes + speakTagClose;
		return watsonText;
	}

	public void getNoAudioFile(AudioType audioType, Voice voice, HttpServletResponse response) {
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;

		String type = StringUtils.lowerCase(audioType.name());
		String fileName = getSpeechDir() + "noaudio." + type + "." + voice.getName() + ".ogg";
		
		try {
			audioFile = new File(fileName);
			path = audioFile.toPath();
		} catch (Exception ex) {
	    	logService.addException(ex);
	    	return;
		}

		if (audioFile.exists()) {
			try {
				outStream = response.getOutputStream();
		    	Files.copy(path, outStream);
		    	outStream.flush();
		    	outStream.close();
		    } catch (Exception ex) {
		    	closeStream(outStream);
		    	logService.addException(ex);
		    }
		}
	}
	
	public void getNoAudioFiles() {
		InputStream inStream = null;
		File audioFile = null;
		String fileName = "";
		Locale locales[] = new Locale[] {Locale.ENGLISH, Locale.FRANCE};
		Object[] obj = new String[] {null};
		
		if (!isTtsAvailable())
			return;
				
		for (Locale locale : locales) {
			List<Voice> voices = getVoices(locale);
			for (Voice voice : voices) {
				for (AudioType audioType : AudioType.values()) {
					String type = StringUtils.lowerCase(audioType.name());
					obj[0] = messages.getMessage("report."+type, null, type, locale);
					String text = messages.getMessage("recipe.view.noaudio", obj, null, locale);
					
					fileName = getSpeechDir() + "noaudio." + type + "." + voice.getName() + ".ogg";
					audioFile = new File(fileName);
					if (audioFile.exists())
						continue;
	
					//get audio from Watson then save it to a file
					try {
						inStream = ttsService.synthesize(text, voice, audioFormat).execute();
						if (inStream != null) {
							FileUtils.copyInputStreamToFile(inStream, audioFile);
						}
					} catch (Exception ex) {
						logService.addException(ex);
						break;
					} finally {
					    closeStream(inStream);
					}
				}
				String name = voice.getName();
				fileName = getSpeechDir() + "sample." + name + ".ogg";
				audioFile = new File(fileName);
				if (!audioFile.exists()) {
					//get audio from Watson then save it to a file
					String desc = voice.getDescription();
					obj[0] = StringUtils.substringBefore(desc, ":");
					String text = messages.getMessage("profile.voice.sample", obj, null, locale);
					try {
						inStream = ttsService.synthesize(text, voice, audioFormat).execute();
						if (inStream != null) {
							FileUtils.copyInputStreamToFile(inStream, audioFile);
						}
					} catch (Exception ex) {
						logService.addException(ex);
						break;
					} finally {
					    closeStream(inStream);
					}
				}
			}
		}		
	}
	
	public List<Voice> getVoices(Locale locale) {
		List<Voice> localeVoices = new ArrayList<Voice>();
		
		if (!isTtsAvailable())
			return null;
		
		String localeLang = locale.getLanguage();
		List<Voice> voices = ttsService.getVoices().execute();
		
		for (Voice voice : voices) {
			String voiceLang = voice.getLanguage().substring(0, 2);
			if (StringUtils.equalsIgnoreCase(localeLang, voiceLang))
				localeVoices.add(voice);
		}
		
		return localeVoices;
	}
	
	private void closeStream(Closeable closeable) {
	    if (closeable != null) {
	        try {
	            closeable.close();
	        } catch (IOException e) {
	            // ignore
	        }
	    }	      	   
	}
}
