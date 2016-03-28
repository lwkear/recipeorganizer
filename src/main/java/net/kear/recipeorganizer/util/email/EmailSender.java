package net.kear.recipeorganizer.util.email;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import net.kear.recipeorganizer.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Component
public class EmailSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private MessageSource messages;
	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private Environment env;
	@Autowired
	private Configuration freemarkerConfig;
	@Autowired
	private ServletContext servletContext;

    private String subjCode = "";
    private String msgCode = "";
    private List<String> msgCodes;
    private Locale locale;
    private User user;
    
	public EmailSender() {}

	public void setSubjectCode(String code) {
		this.subjCode = code;
	}
		
	public void setMessageCode(String code) {
		this.msgCode = code;
	}
	
	public void setMessageCodes(List<String> codes) {
		this.msgCodes = codes;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void sendSimpleEmailMessage() throws MailException {
    	logger.debug("sendSimpleEmailMessage");
    	
        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
        
        //MimeMessage msg = mailSender.createMimeMessage();        
        
        final SimpleMailMessage email = new SimpleMailMessage();
        
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(env.getProperty("support.email"));
       	mailSender.send(email);
        
		//throw new MailSendException("sendSimpleEmailMessage forced error");
    }
	
	public void sendSimpleMimeMessage() throws MailException, MessagingException {

        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(user.getEmail());
		helper.setSubject(subject);
		helper.setText(message);
		helper.setFrom(env.getProperty("support.email"));
       	
		FileSystemResource file = new FileSystemResource(new File("g://recipeorganizer//recipe421.pdf"));
        helper.addAttachment("recipe421.pdf", file);  		
		
		mailSender.send(mimeMessage);
	}

	public void sendFreemarkeMessage() throws MailException, MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {

        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();

		/*MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo("lkear@outlook.com");
		helper.setFrom(env.getProperty("support.email"));
		helper.setSubject(subject);*/
		
     	//<a href="http://localhost:8080/recipeorganizer/home"><img src="http://localhost:8080/recipeorganizer/resources/logo4.png"></a>
     	
     	/*DefaultResourceLoader loader = new DefaultResourceLoader();
     	loader.getResource(location)*/
     	
     	//this worked locally but did not appear in gmail
     	
		//File img = new File(servletContext.getRealPath("/resources/logo3.png"));
		//<img alt="RecipeOrganizer" src="${imgAsBase64}" />
     	
     	/*byte[] imgBytes = null;
		try {
			imgBytes = IOUtils.toByteArray(new FileInputStream(img));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
     	byte[] imgBytesAsBase64 = Base64.encodeBase64(imgBytes);
     	String imgDataAsBase64 = new String(imgBytesAsBase64);
     	String imgAsBase64 = "data:image/png;base64," + imgDataAsBase64;*/     	

		//map.put("imgAsBase64", imgAsBase64);
        //helper.setText(out.toString(), true);

		//FileSystemResource file = new FileSystemResource(new File("g://recipeorganizer//recipe421.pdf"));		
        //helper.addAttachment("recipe421.pdf", file);		
		
		mimeMessage.setFrom(new InternetAddress(env.getProperty("support.email")));
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("lkear@outlook.com"));
		mimeMessage.setSubject(subject);

		Template template = freemarkerConfig.getTemplate("shareRecipe.ftl");
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Pumpkin Bundt Cake");
		map.put("description", "The best recipe ever!");
		Writer out = new StringWriter();
        template.process(map, out);		

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(out.toString(), "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds inline image attachments
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setHeader("Content-ID", "<rologo>");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        String imageFilePath = servletContext.getRealPath("/resources/logo3.png");
        try {
        	imagePart.attachFile(imageFilePath);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        multipart.addBodyPart(imagePart);
       	
        MimeBodyPart pdfPart = new MimeBodyPart();
        pdfPart.setDisposition(MimeBodyPart.ATTACHMENT);
        String pdfPath = "g://recipeorganizer//recipe421.pdf";
        try {
        	pdfPart.attachFile(pdfPath);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        multipart.addBodyPart(pdfPart);
        
        mimeMessage.setContent(multipart);
		mailSender.send(mimeMessage);
	}

	public void sendTokenEmailMessage(String msgLink) throws MailException {
    	logger.debug("sendTokenEmailMessage");
    	
        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
        
        final SimpleMailMessage email = new SimpleMailMessage();
        
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message + " \r\n\r\n" + msgLink);
        email.setFrom(env.getProperty("support.email"));
       	mailSender.send(email);
        
		//throw new MailSendException("sendTokenEmailMessage forced error");
    }

	public void sendTokenEmail(EmailMessage emailMessage) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		try {
			mimeMessage.setFrom(new InternetAddress(emailMessage.getSenderEmail()));
			mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailMessage.getRecipientEmail()));
			mimeMessage.setSubject(emailMessage.getSubject());

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailMessage.getBody(), "text/html");

	        // creates multi-part
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
 
	        // adds inline image attachments
	        MimeBodyPart imagePart = new MimeBodyPart();
	        imagePart.setHeader("Content-ID", "<rologo>");
	        imagePart.setDisposition(MimeBodyPart.INLINE);
	        String imageFilePath = servletContext.getRealPath("/resources/logo3.png");
	       	imagePart.attachFile(imageFilePath);
	        multipart.addBodyPart(imagePart);

	        imagePart = new MimeBodyPart();
	        imagePart.setHeader("Content-ID", "<cleargif>");
	        imagePart.setDisposition(MimeBodyPart.INLINE);
	        imageFilePath = servletContext.getRealPath("/resources/clear.gif");
	       	imagePart.attachFile(imageFilePath);
	        multipart.addBodyPart(imagePart);

	        mimeMessage.setContent(multipart);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
        
        mailSender.send(mimeMessage);
	}
	
	
	
	private String getMessage() {
		String message = "";
		
        if (msgCode != null) {
        	message = messages.getMessage(msgCode, null, locale);
        }
        else {
        	if (!msgCodes.isEmpty()) {
		        for (String code : msgCodes) {
		        	String msg = messages.getMessage(code, null, locale);
		        	message += msg + " \r\n\r\n ";
		        }
        	}
    	}
		
		return message;
	}
	
	/*private void initialize() {
	    subjCode = "";
	    msgCode = "";
	    msgCodes.clear();
	    locale = null;
	    user = null;
	}*/
}
