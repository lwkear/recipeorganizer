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

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
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
	private final static String[] keywords = {"ingredient","ingredients","instruction","instructions","note","notes","private"};
	private String watsonTTSUsername = "";
	private String watsonTTSPassword = "";
	private String watsonSTTUsername = "";
	private String watsonSTTPassword = "";
	private String watsonConvUsername = "";
	private String watsonConvPassword = "";
	private String watsonConvWorkspaceId = "";
	private boolean watsonInitialized = false;
	private boolean watsonTTSAvailable = true;
	private boolean watsonSTTAvailable = true;
	private boolean watsonConvAvailable = true;
	private TextToSpeech ttsService = null;
	private SpeechToText sttService = null;
	private ConversationService convService = null; 
	private String speechDir = "";
	
	public SpeechUtilImpl() {}

	public void initWatson() {
		logger.debug("initWatson");
		//testing appears to show that no exception is thrown initializing a service
		//retaining the try/catch anyway
		try {
			ttsService = new TextToSpeech(watsonTTSUsername, watsonTTSPassword);
		} catch (Exception ex) {
			logger.debug("TextToSpeech init(): " + ex.getMessage(), ex);
			logService.addException(ex);
			watsonTTSAvailable = false;
		}

		try {
			sttService = new SpeechToText(watsonSTTUsername, watsonSTTPassword);
		} catch (Exception ex) {
			logger.debug("SpeechToText init(): " + ex.getMessage(), ex);
			logService.addException(ex);
			watsonSTTAvailable = false;
		}

		try {
			convService = new ConversationService(ConversationService.VERSION_DATE_2016_09_20, watsonConvUsername, watsonConvPassword);
		} catch (Exception ex) {
			logger.debug("Conversation init(): " + ex.getMessage(), ex);
			logService.addException(ex);
			watsonConvAvailable = false;
		}
	}
	
	@EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
		logger.debug("handleContextRefresh: watsonInitialized=" + watsonInitialized);
		//this routine gets called twice for the two contexts, hence the flag
		if (watsonInitialized)
			return;
		
		initWatson();
		getDefaultAudioFiles();
		watsonInitialized = true;
    }	

	public void setWatsonTTSAccount(String username, String password) {
		this.watsonTTSUsername = username;
		this.watsonTTSPassword = password;
	}

	public void setWatsonSTTAccount(String username, String password) {
		this.watsonSTTUsername = username;
		this.watsonSTTPassword = password;
	}

	public void setWatsonConvAccount(String username, String password, String workspaceId) {
		this.watsonConvUsername = username;
		this.watsonConvPassword = password;
		this.watsonConvWorkspaceId = workspaceId;
	}
	
	public void setSpeechDir(String dir) {
		this.speechDir = dir;
	}
	
	public String getSpeechDir() {
		return this.speechDir;
	}
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public boolean isWatsonTTSAvailable() {
		return this.watsonTTSAvailable;
	}
	
	public boolean isWatsonSTTAvailable() {
		return this.watsonSTTAvailable;
	}
	
	public boolean isWatsonConvAvailable() {
		return this.watsonConvAvailable;
	}

	public void setWatsonTTSAvailable(boolean watsonTTSAvailable) {
		this.watsonTTSAvailable = watsonTTSAvailable;
	}

	public void setWatsonSTTAvailable(boolean watsonSTTAvailable) {
		this.watsonSTTAvailable = watsonSTTAvailable;
	}

	public void setWatsonConvAvailable(boolean watsonConvAvailable) {
		this.watsonConvAvailable = watsonConvAvailable;
	}

	/**********************/
	/*** Text to Speech ***/
	/**********************/
	public void watsonUnavailable(Voice voice, HttpServletResponse response) {
		String fileName = "conversation.notavailable." + voice.getName() + ".ogg";
		//must set the status first or otherwise Spring returns the SC_OK status
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		getAudio(fileName, response);		
	}
	
	public boolean getRecipeAudio(String fileName, String text, Voice voice, DateTime recipeDate, HttpServletResponse response) {
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
		
		if (isWatsonTTSAvailable()) {
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
				logger.debug("getRecipeAudio(): " + ex.getMessage(), ex);
				logService.addException(ex);
			} finally {
			    closeStream(inStream);
			    closeStream(outStream);
			}
		}
		
		return result;
	}

	public boolean getAudio(String fileName, String text, Voice voice, boolean saveFile, HttpServletResponse response) {
		InputStream inStream = null;
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;
		boolean result = false;

		if ((StringUtils.isBlank(text) || voice == null) ||
			(saveFile && StringUtils.isBlank(fileName))) 
			return result;
		
		if (saveFile) {
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
		
		if (isWatsonTTSAvailable()) {
			//get audio from Watson and copy to response
			try {
				inStream = ttsService.synthesize(text, voice, audioFormat).execute();
				if (inStream != null) {
					if (saveFile) {
						FileUtils.copyInputStreamToFile(inStream, audioFile);
						outStream = response.getOutputStream();
				    	Files.copy(path, outStream);
				    	outStream.flush();
				    	result = true;
					}
					else {
						outStream = response.getOutputStream();
				        byte[] buffer = new byte[2048];
				        int read;
				        while ((read = inStream.read(buffer)) != -1) {
				        	outStream.write(buffer, 0, read);
				        }
				        outStream.flush();
				        result = true;
					}
				}
			} catch (Exception ex) {
				logger.debug("getAudio(): " + ex.getMessage(), ex);
				logService.addException(ex);
			} finally {
			    closeStream(inStream);
			    closeStream(outStream);
			}
		}
		
		return result;
	}

	public boolean getAudio(String name, HttpServletResponse response) {
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;

		String fileName = getSpeechDir() + name;
		
		try {
			audioFile = new File(fileName);
			path = audioFile.toPath();
		} catch (Exception ex) {
	    	logService.addException(ex);
	    	return false;
		}

		if (audioFile.exists()) {
			try {
				outStream = response.getOutputStream();
		    	Files.copy(path, outStream);
		    	outStream.flush();
		    	outStream.close();
		    } catch (Exception ex) {
		    	logService.addException(ex);
		    	closeStream(outStream);		    	
		    	return false;
		    }
		}
		
		return true;
	}
	
	public boolean getNoAudio(AudioType audioType, Voice voice, HttpServletResponse response) {
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
	    	return false;
		}

		if (audioFile.exists()) {
			try {
				outStream = response.getOutputStream();
		    	Files.copy(path, outStream);
		    	outStream.flush();
		    	outStream.close();
		    } catch (Exception ex) {
		    	logService.addException(ex);
		    	closeStream(outStream);		    	
		    	return false;
		    }
		}
		
		return true;
	}

	public boolean getSample(String fileName, HttpServletResponse response) {
		ServletOutputStream outStream = null;
		File audioFile = null;
		Path path = null;
		
		if (StringUtils.isBlank(fileName))
			return false;
		
		//prepare the output file
		try {
			audioFile = new File(fileName);
			path = audioFile.toPath();
		} catch (Exception ex) {
	    	logService.addException(ex);
	    	return false;
		}
		
		//the audio file should already exist so simply return it in the response
		if (audioFile.exists()) {
			try {
				outStream = response.getOutputStream();
		    	Files.copy(path, outStream);
		    	outStream.flush();
		    	outStream.close();
		    } catch (Exception ex) {
		    	logService.addException(ex);
		    	closeStream(outStream);
		    	return false;
		    }
		}
		
		return true;
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

	public void getDefaultAudioFiles() {
		String fileName = "";
		String text = "";
		Locale locales[] = new Locale[] {Locale.ENGLISH, Locale.FRANCE};
		Object[] obj = new String[] {null};
		
		if (!isWatsonTTSAvailable())
			return;
				
		for (Locale locale : locales) {
			List<Voice> voices = getVoices(locale);
			if (voices == null)
				return;
			for (Voice voice : voices) {
				for (AudioType audioType : AudioType.values()) {
					String type = StringUtils.lowerCase(audioType.name());
					obj[0] = messages.getMessage("watson."+type, null, type, locale);
					text = messages.getMessage("watson.noaudio", obj, null, locale);
					fileName = getSpeechDir() + "noaudio." + type + "." + voice.getName() + ".ogg";
					if (!createFile(text, fileName, voice))
						break;
					if ((audioType == AudioType.NOTES)	||
						(audioType == AudioType.PRIVATENOTES)) {
						text = messages.getMessage("watson." + type + ".notavailable", null, null, locale);
						fileName = getSpeechDir() + "notavailable." + type + "." + voice.getName() + ".ogg";
						if (!createFile(text, fileName, voice))
							break;
					}
				}
				String name = voice.getName();
				String desc = voice.getDescription();
				obj[0] = StringUtils.substringBefore(desc, ":");
				text = messages.getMessage("profile.voice.sample", obj, null, locale);
				fileName = getSpeechDir() + "sample." + name + ".ogg";
				if (!createFile(text, fileName, voice))
					break;
				text = messages.getMessage("watson.watson.notavailable", null, null, locale);
				fileName = getSpeechDir() + "conversation.notavailable." + name + ".ogg";
				if (!createFile(text, fileName, voice))
					break;
			}
		}		
	}
	
	public List<Voice> getVoices(Locale locale) {
		List<Voice> localeVoices = new ArrayList<Voice>();
		List<Voice> voices = null;
		
		if (!isWatsonTTSAvailable()) {
			logger.debug("getVoices: WatsonTTSAvailable = false");
			return null;
		}
		
		String localeLang = locale.getLanguage();
		try {
			voices = ttsService.getVoices().execute();
	    } catch (Exception ex) {
	    	logger.debug("getVoices(): " + ex.getMessage(), ex);
	    	logService.addException(ex);
	    	return null;
	    }
		
		for (Voice voice : voices) {
			String voiceLang = voice.getLanguage().substring(0, 2);
			if (StringUtils.equalsIgnoreCase(localeLang, voiceLang))
				localeVoices.add(voice);
		}
		
		return localeVoices;
	}

	private boolean createFile(String text, String fileName, Voice voice) {
		InputStream inStream = null;
		File audioFile = null;
		boolean result = true;

		audioFile = new File(fileName);
		if (audioFile.exists())
			return result;

		if (!isWatsonTTSAvailable())
			return false;
		
		//get audio from Watson then save it to a file
		try {
			inStream = ttsService.synthesize(text, voice, audioFormat).execute();
			if (inStream != null) {
				FileUtils.copyInputStreamToFile(inStream, audioFile);
			}
		} catch (Exception ex) {
			logger.debug("createFile(): " + ex.getMessage(), ex);
			logService.addException(ex);
			result = false;
		} finally {
		    closeStream(inStream);
		}
		
		return result;
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

	/**********************/
	/*** Speech to Text ***/
	/**********************/
	public String getSTTToken() {
		String token = "";

		if (!isWatsonSTTAvailable())
			return token;
		
		try {
			token = sttService.getToken().execute();			
		} catch (Exception ex) {
			logger.debug("getSTTToken(): " + ex.getMessage(), ex);
			logService.addException(ex);
		}
		
		return token;
	}
	
	/********************/
	/*** Conversation ***/
	/********************/
	public MessageResponse sendWatsonRequest(MessageRequest message) {
		MessageResponse response = null;

		if (!isWatsonConvAvailable())
			return response;
		
		try {
			response = convService.message(watsonConvWorkspaceId, message).execute();
		} catch (Exception ex) {
			logger.debug("sendWatsonRequest(): " + ex.getMessage(), ex);
			logService.addException(ex);
		}

		return response;
	}
}
