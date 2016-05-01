package net.kear.recipeorganizer.util.email;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private JavaMailSender mailSender;
	@Autowired
	private ServletContext servletContext;

	public EmailSender() {}

	public void sendHtmlEmail(EmailMessage emailMessage) throws AddressException, MessagingException, IOException {
		logger.debug("sendHtmlEmail");

		MimeMessage mimeMessage = mailSender.createMimeMessage();

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
        String imageFilePath = servletContext.getRealPath("/resources/images/logo.png");
       	imagePart.attachFile(imageFilePath);
        multipart.addBodyPart(imagePart);

        imagePart = new MimeBodyPart();
        imagePart.setHeader("Content-ID", "<cleargif>");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        imageFilePath = servletContext.getRealPath("/resources/images/clear.png");
       	imagePart.attachFile(imageFilePath);
        multipart.addBodyPart(imagePart);

        if (emailMessage.isPdfAttached()) {
	        MimeBodyPart pdfPart = new MimeBodyPart();
	        pdfPart.setDisposition(MimeBodyPart.ATTACHMENT);
        	pdfPart.attachFile(emailMessage.getPdfFileName());
	        multipart.addBodyPart(pdfPart);
        }
        
        mimeMessage.setContent(multipart);
        mailSender.send(mimeMessage);
	}
}
