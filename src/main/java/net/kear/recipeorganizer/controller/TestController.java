package net.kear.recipeorganizer.controller;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserMessageService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.report.ReportGenerator;
import net.kear.recipeorganizer.util.EncryptionUtil;
import net.kear.recipeorganizer.util.SpeechUtil;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.AccountChangeEmail;
import net.kear.recipeorganizer.util.email.AccountChangeEmail.ChangeType;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.InvitationEmail;
import net.kear.recipeorganizer.util.email.PasswordEmail;
import net.kear.recipeorganizer.util.email.RegistrationEmail;
import net.kear.recipeorganizer.util.email.ShareRecipeEmail;
import net.kear.recipeorganizer.util.maint.MaintenanceUtil;

@Controller
public class TestController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserInfo userInfo;
	@Autowired
	private MessageSource messages;
	@Autowired
	private UserService userService;
	@Autowired
	private ConstraintMap constraintMap;
	@Autowired
	MaintenanceUtil maintUtil;
	@Autowired
	private ReportGenerator reportGenerator; 
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private RegistrationEmail registrationEmail; 
	@Autowired
	private PasswordEmail passwordEmail; 
	@Autowired
	private AccountChangeEmail accountChangeEmail; 
	@Autowired
	private ShareRecipeEmail shareRecipeEmail;
	@Autowired
	private InvitationEmail invitationEmail;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private Environment env;
	@Autowired
	private UserMessageService userMessageService;
	@Autowired
	DataSource dataSource;
	@Autowired
	EncryptionUtil encryptUtil;
	@Autowired
	SpeechUtil speechUtil;
	@Autowired
	RecipeService recipeService;

	/*****************/
	/*** test page ***/
	/**/
/*
	@RequestMapping(value = "/test/getAudio", method = RequestMethod.GET)
	public void getAudio(@RequestParam("recipeId") final Long recipeId, @RequestParam("section") final Integer section, HttpServletResponse response, Locale locale) throws Exception {
		logger.debug("getOggAudio");

		TextToSpeech service = new TextToSpeech("7d42a907-17c9-4006-9fc8-9b351563df04", "vK27rCEP7Uyy");
		
		String ingredText = "<speak version='1.0'>1 cup sugar <break time='3s'/>2 cups flour <break time='3s'/>"
				+ "1/2 teaspoon salt <break time='3s'/>1 teaspoon vanilla<break time='3s' />1 pound chicken breast <break time='3s'/>"
				+ "1 tablespoon olive oil <break time='3s'/>8 ounces dark chocolate <break time='3s'/>done</speak>";
		
		InputStream oggStream = service.synthesize(ingredText, Voice.EN_MICHAEL, AudioFormat.OGG_VORBIS).execute();

		File oggFile = new File("G:\\recipeorganizer\\test.ogg");
		try {
			FileUtils.copyInputStreamToFile(oggStream, oggFile);
		} catch (IOException e) {}
		
        try {
        	Path path = oggFile.toPath();
        	ServletOutputStream stream = response.getOutputStream();
        	Files.copy(path, stream);
        	stream.flush();
        	stream.close();
        	logger.debug("Successful download");                
        } catch (IOException ex) {
        	throw new Exception(ex);
        }

		String ingredText = "";
		Recipe recipe = recipeService.getRecipe(recipeId);
		IngredientSection ingredSection = recipe.getIngredSections().get(section);
		List<RecipeIngredient> ingreds = ingredSection.getRecipeIngredients();
		ingredText = speechUtil.prepareIngredients(ingreds, 0);
		logger.debug("watson text: " + ingredText);
	
		try {
			speechUtil.getAudio(ingredText, Voice.EN_MICHAEL, response);
		} catch (Exception e) {
			logger.debug("got error: " + e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
		
		InputStream inStream = null;
		ServletOutputStream outStream = null;
		
		try {
			TextToSpeech service = new TextToSpeech("7d42a907-17c9-4006-9fc8-9b351563df04", "vK27rCEP7Uyy");
		
			inStream = service.synthesize(ingredText, Voice.EN_MICHAEL, AudioFormat.OGG_VORBIS).execute();
			
			outStream = response.getOutputStream();
	        byte[] buffer = new byte[2048];
	        int read;
	        while ((read = inStream.read(buffer)) != -1) {
	        	outStream.write(buffer, 0, read);
	        }
	        outStream.flush();
		} catch (Exception e) {
			logger.debug("got error: " + e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
		    close(inStream);
		    close(outStream);
		}
	}
		
*/
	/*private void close(Closeable closeable) {
	    if (closeable != null) {
	        try {
	            closeable.close();
	        } catch (IOException e) {
	            // ignore
	        }
	    }	      	   
	}*/

	@RequestMapping(value = "/test/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model, HttpServletRequest request, Locale locale) {
		logger.debug("getTestpage");
		
		/* 
		*****************************************
	 	* encryption test
	 	*****************************************
		String passphrase = "correct horse battery staple";
		byte[] salt = "choose a better salt".getBytes();
		int iterations = 10000;
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecretKey tmp = null;
		try {
			tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");		
		
		Cipher aes = null;
		byte[] ciphertext = null;
		try {
			aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		try {
			aes.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			ciphertext = aes.doFinal("lkear@outlook.com".getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		String encryptText = Base64.encodeBase64URLSafeString(ciphertext);
		// escapes for url
		//encryptText = encryptText.replace('+', '-').replace('/', '_').replace("%", "%25").replace("\n", "%0A");		
		//String input = encryptText.replace("%0A", "\n").replace("%25", "%").replace('_', '/').replace('-', '+');
		
		String input = encryptText;
		byte[] dec = Base64.decodeBase64(input.getBytes());
		String decryptText = "";
		
		try {
			aes.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		try {
			decryptText = new String(aes.doFinal(dec));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("encryptText", encryptText);
		model.addAttribute("decryptText", decryptText);
		*/
		
		/*String enc = encryptUtil.encryptURLParam("lkear@outlook.com");
		String dec = encryptUtil.decryptURLParam(enc);
		model.addAttribute("encryptText", enc);
		model.addAttribute("decryptText", dec);*/
		
		
		/*try {
			dataSource.getConnection();
		} catch (SQLException ex) {
			logger.debug("dataSource connection error");
			logger.error(ex.getClass().toString(), ex);
		}*/

		/* 
		*****************************************
	 	* joda date/time formatting test
	 	*****************************************
		DateTime dt = new DateTime();
		DateTimeFormatter fmt = DateTimeFormat.fullDate();
		DateTimeFormatter fmtLocale = fmt.withLocale(locale);
		String strFull = fmtLocale.print(dt);
		fmt = DateTimeFormat.fullTime();
		fmtLocale = fmt.withLocale(locale);
		strFull = strFull + " " + fmtLocale.print(dt);
		
		model.addAttribute("fullDate", strFull);
		
		fmt = DateTimeFormat.mediumDate();
		fmtLocale = fmt.withLocale(locale);
		String strMed = fmtLocale.print(dt);
		fmt = DateTimeFormat.mediumTime();
		fmtLocale = fmt.withLocale(locale);
		strMed = strMed + " " + fmtLocale.print(dt);
		
		model.addAttribute("medDate", strMed);

		fmt = DateTimeFormat.shortTime();
		fmtLocale = fmt.withLocale(locale);
		String strShort = fmtLocale.print(dt);
		
		model.addAttribute("shrtDate", strShort);
		*****************************************
	 	* end test
	 	*****************************************
		*/
		
		/*String text = "celery, and ½ teaspoon salt";
		model.addAttribute("text", text);
		
		WebGreeting wg = new WebGreeting();
		model.addAttribute("webGreeting", wg);

        model.addAttribute("message", messages.getMessage("user.register.sentToken", null, "Token sent", locale));

		User currentUser = (User)userInfo.getUserDetails();
		
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
        
		//Date lastLogin = user.getLastLogin();
		//if (lastLogin == null)
		Date lastLogin = user.getDateAdded();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastLogin);
		cal.add(Calendar.DATE, 365);
		
		Calendar tdy = Calendar.getInstance();
		
        if (tdy.getTime().getTime() > cal.getTime().getTime())
        	model.addAttribute("expired", "account has expired");
        else
        	model.addAttribute("expired", "account valid");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fmt = sdf.format(lastLogin);
        logger.debug("lastLogin: " + fmt);
        fmt = sdf.format(cal.getTime());
        logger.debug("lastLogin + 365: " + fmt);
        fmt = sdf.format(tdy.getTime());
        logger.debug("today: " + fmt);*/
		
		/*MaintenanceDto maintDto = new MaintenanceDto();
		model.addAttribute("maintenanceDto", maintDto);
		model.addAttribute("dayMap", maintUtil.getWeekMap(locale));*/
		
		/*User user = (User)userInfo.getUserDetails();
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.signupSubject");
     	emailSender.setMessageCode("user.email.signupSuccess");

     	try {
			emailSender.sendFreemarkeMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		/*EmailDetail emailDetail = new EmailDetail("Gene Kear", "kear.larry@gmail.com", locale);
		emailDetail.setTokenUrl("/confirmRegistration?token=a72ad4cc-5772-4f5f-8fd6-9707aa85f920");
        try {
        	registrationEmail.constructEmail(emailDetail);
        	emailSender.sendHtmlEmail(emailDetail);
        } catch (Exception ex) {}

        emailDetail = new EmailDetail("William Kear", "kear.larry@gmail.com", locale);
        String confirmationUrl = "/confirmPassword?id=131&token=a72ad4cc-5772-4f5f-8fd6-9707aa85f920";
		emailDetail.setTokenUrl(confirmationUrl);
		try {
			passwordEmail.constructEmail(emailDetail);
			emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {}
		
		emailDetail = new EmailDetail("Larry Kear", "lkear@outlook.com", locale);
    	emailDetail.setChangeType(ChangeType.PASSWORD);
    	try {
    		accountChangeEmail.constructEmail(emailDetail);
    		emailSender.sendHtmlEmail(emailDetail);
    	} catch (Exception ex) {}

    	emailDetail = new EmailDetail("Peggy McKinney", "kear.larry@gmail.com", locale);
    	emailDetail.setChangeType(ChangeType.PROFILE);
    	try {
    		accountChangeEmail.constructEmail(emailDetail);
    		emailSender.sendHtmlEmail(emailDetail);
    	} catch (Exception ex) {}
		
    	emailDetail = new EmailDetail("Ilsa Kear", "kear.larry@gmail.com", locale);
    	emailDetail.setTokenUrl("/confirmRegistration?token=a72ad4cc-5772-4f5f-8fd6-9707aa85f920");
    	try {
    		invitationEmail.constructEmail(emailDetail);
    		emailSender.sendHtmlEmail(emailDetail);
    	} catch (Exception ex) {}

    	emailDetail = new EmailDetail("Hans Kear", "hans@gmail.com", locale);
		emailDetail.setUserName("Peggy McKinney");
		emailDetail.setUserFirstName("Peggy");
		emailDetail.setUserMessage("This is a test.");
		emailDetail.setRecipeName("Test with sections");
		emailDetail.setPdfAttached(true);
		emailDetail.setPdfFileName("G:/recipeorganizer/pdfs/recipe11.pdf");
    	try {
    		shareRecipeEmail.constructEmail(emailDetail);
			emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {}*/
    	
    	/*reportGenerator.createRecipePDF(741L, locale);
    	reportGenerator.createRecipePDF(1463L, locale);
    	reportGenerator.createRecipePDF(1101L, locale);
    	reportGenerator.createRecipePDF(1522L, locale);
    	reportGenerator.createRecipePDF(1141L, locale);
    	reportGenerator.createRecipePDF(421L, locale);
    	reportGenerator.createRecipePDF(1462L, locale);*/

		//reportGenerator.configureReports(servletContext, env);

		/*UserAge[] ages = UserAge.values();
		for (UserAge age : ages)
			logger.debug("ageValue:" + age);*/
		
		/*List<UserAge> ageList = UserAge.list();
		for (UserAge age : ageList)
			logger.debug("ageList:" + age);
		
		String age = UserAge.UA18TO30.toString();
		logger.debug("age:" + age);
				
		String[] strList = UserAge.strList();
		for (String strAge : strList)
			logger.debug("ageStrList:" + strAge);
		
		age = strList[UserAge.UA31TO50.getValue()];
		logger.debug("age:" + age);

		age = strList[UserAge.UA51TO70.ordinal()];
		logger.debug("age:" + age);*/

		/*User user = userService.getUser(5L);
		
		RecipeMessageDto recipeMessageDto = new RecipeMessageDto();
		model.addAttribute("recipeMessageDto", recipeMessageDto);
		model.addAttribute("approvalActions", ApprovalAction.list());

		UserMessage msg = new UserMessage();
		msg.setFromUserId(3L);
		msg.setToUserId(5L);
		msg.setRecipeId(null);
		msg.setViewed(false);
		msg.setMessage("Some regular text, then some html");
		msg.setHtmlMessage("<p>This is a message!</p><ul><li>Reason #1</li><li>Reason #2</li><li>Reason #3</li></ul><p>Some other message</p>");

		userMessageService.addMessage(msg);*/
		
		return "test/testpage";
	}

	@RequestMapping(value = "/test/testpage", method = RequestMethod.POST)
	public String postTestpage(@ModelAttribute @Valid WebGreeting wb, BindingResult result) {
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "test/testpage";
		}
		
		logger.debug("getTestpage");
		logger.debug("wb.greeting:" + wb.getGreeting());
		
		return "test/testpage";
	}
	
	public static class WebGreeting {
		
		//@Size(max=20)
		private String greeting;

		public WebGreeting() {}

		public String getGreeting() {
			return greeting;
		}

		public void setGreeting(String greeting) {
			this.greeting = greeting;
		}
	}
}


/*
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.debug("getStartpage");

		return "start";
	}*/

/*
UserDto user = new UserDto();
//user.setEmail("ilsa@gmail.com");
user.setEmail(null);
user.setFirstName("Ilsa");
user.setLastName("Kear");
user.setPassword("$2a$10$btkjPF8CVqS1v5W8Hh5qrujLSTyhVAXAA5YHbEm2lP1m3lp46DMnC");

try {
	userService.addUser(user);
} catch (Exception ex) {
	logger.debug("exception class: " + ex.getClass().toString());
	String msg = ExceptionUtils.getMessage(ex);
	logger.debug("msg: " + msg);
	Throwable excptn = ExceptionUtils.getRootCause(ex);
	if (excptn != null) {
		msg = ExceptionUtils.getRootCause(ex).getClass().toString();
		logger.debug("root class: " + msg);
		msg = ExceptionUtils.getRootCauseMessage(ex);
		logger.debug("root msg: " + msg);
	}
}

try {
	userService.deleteUser(78L);
} catch (Exception ex) {
	logger.debug("exception class: " + ex.getClass().toString());
	String msg = ExceptionUtils.getMessage(ex);
	logger.debug("msg: " + msg);
	Throwable excptn = ExceptionUtils.getRootCause(ex);
	if (excptn != null) {
		msg = ExceptionUtils.getRootCause(ex).getClass().toString();
		logger.debug("root class: " + msg);
		msg = ExceptionUtils.getRootCauseMessage(ex);
		logger.debug("root msg: " + msg);
	}
}			
*/

/*

		logger.debug("1 valid: " + isValid("1"));
		logger.debug("1/ valid: " + isValid("1/"));
		logger.debug("1/2 valid: " + isValid("1/2"));
		logger.debug("1. valid: " + isValid("1."));
		logger.debug("1.1 valid: " + isValid("1.1"));
		logger.debug("1, valid: " + isValid("1,"));
		logger.debug("a valid: " + isValid("a"));
		logger.debug("1a valid: " + isValid("1a"));
		logger.debug("1 1/2 valid: " + isValid("1 1/2"));
		logger.debug("a1 valid: " + isValid("a1"));
		logger.debug("/ valid: " + isValid("/"));
		logger.debug(". valid: " + isValid("."));
		logger.debug(", valid: " + isValid(","));
		
public boolean isValid(String qty) {
	String str = qty;
	str = str.trim();
	
	if (str == null) {
        return false;
    }
    int length = str.length();
    if (length == 0) {
        return false;
    }
    if ((str.charAt(0) == '/') || (str.charAt(0) == ',') || (str.charAt(0) == '.')) {
        if (length == 1) {
            return false;
        }
    }
    for (int i=0; i < length; i++) {
        char c = str.charAt(i);
        if ((c < '0' || c > '9') && (c != '/' && c != '.' && c != ',' && c != ' ')) {
        	return false;
        }
    }
    for (int i=0; i < length; i++) {
        char c = str.charAt(i);
        if ((c == '/') && (i+1 >= length))
        	return false;
    }
    
    Fraction fract;
    str = str.replace(',', '.');
    try {
    	fract = Fraction.getFraction(str);
    } catch (NumberFormatException ex) {
    	logger.debug("Exception: " + qty);
    	return false;
    }
    
	float value = fract.floatValue();
    
    return true;
}

		//Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserDto.class); 
		//model.addAttribute("sizeMap", sizeMap);

		/*Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				new Class[] {Recipe.class, RecipeIngredient.class, Ingredient.class, Source.class, InstructionSection.class, 
							IngredientSection.class});
							
*/ 

/*
@RequestMapping(value = "/test/getOggSampleAudio", method = RequestMethod.GET)
public void getOggSampleAudio(HttpServletResponse response, Locale locale) throws Exception {
	logger.debug("getOggSampleAudio");

	File oggFile = new File("G:\\recipeorganizer\\sample.ogg");
    try {
    	Path path = oggFile.toPath();
    	ServletOutputStream stream = response.getOutputStream();
    	Files.copy(path, stream);
    	stream.flush();
    	stream.close();
    	logger.debug("Successful download");                
    } catch (IOException ex) {
    	throw new Exception(ex);
    }
}	

@RequestMapping(value = "/test/getWavAudio", method = RequestMethod.GET)
public void getWavAudio(HttpServletResponse response, Locale locale) throws Exception {
	logger.debug("getWavAudio");

	TextToSpeech service = new TextToSpeech("7d42a907-17c9-4006-9fc8-9b351563df04", "vK27rCEP7Uyy");
	
	String ingredText = "<speak version='1.0'>1 cup sugar <break time='3s'/>2 cups flour <break time='3s'/>"
			+ "1 teaspoon salt <break time='3s'/>1 teaspoon vanilla<break time='3s' />1 pound chicken breast <break time='3s'/>"
			+ "1 tablespoon olive oil <break time='3s'/>8 ounces dark chocolate <break time='3s'/>done</speak>";
	
	InputStream wavStream = service.synthesize(ingredText, Voice.EN_MICHAEL, AudioFormat.WAV).execute();
	InputStream wavStreamHdr = null;
	try {
		wavStreamHdr = WaveUtils.reWriteWaveHeader(wavStream);
	} catch (IOException e) {
		throw new Exception(e);
	}

	File wavFile = new File("G:\\recipeorganizer\\test.wav");
	if (wavStream != null) {
		try {
			FileUtils.copyInputStreamToFile(wavStreamHdr, wavFile);
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

    try {
    	Path path = wavFile.toPath();
    	ServletOutputStream stream = response.getOutputStream();
    	Files.copy(path, stream);
    	stream.flush();
    	stream.close();
    	logger.debug("Successful download");                
    } catch (IOException ex) {
    	throw new Exception(ex);
    }
}*/	
