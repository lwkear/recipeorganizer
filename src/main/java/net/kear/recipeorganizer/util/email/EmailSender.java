package net.kear.recipeorganizer.util.email;

import java.io.IOException;

import javax.mail.Address;
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
	
	public void sendHtmlEmail(EmailDetail emailDetail) throws AddressException, MessagingException, IOException {
		logger.debug("start sendHtmlEmail to: " + emailDetail.getRecipientEmail());

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		mimeMessage.setFrom(new InternetAddress(emailDetail.getSenderEmail(), emailDetail.getSenderName()));
		Address address = new InternetAddress(emailDetail.getRecipientEmail(), emailDetail.getRecipientName());
		((InternetAddress)address).validate();
		logger.debug("address.validate() w/ personal passed");
		
		mimeMessage.setRecipient(Message.RecipientType.TO, address);
		mimeMessage.setSubject(emailDetail.getSubject());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailDetail.getBody(), "text/html");

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

        if (emailDetail.isPdfAttached()) {
	        MimeBodyPart pdfPart = new MimeBodyPart();
	        pdfPart.setDisposition(MimeBodyPart.ATTACHMENT);
        	pdfPart.attachFile(emailDetail.getPdfFileName());
	        multipart.addBodyPart(pdfPart);
        }
        
        mimeMessage.setContent(multipart);
        mailSender.send(mimeMessage);
        logger.debug("end sendHtmlEmail to: " + emailDetail.getRecipientEmail());
	}
}