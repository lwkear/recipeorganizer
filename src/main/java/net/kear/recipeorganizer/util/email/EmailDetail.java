package net.kear.recipeorganizer.util.email;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.util.email.AccountChangeEmail.ChangeType;

public class EmailDetail {

	private String senderName = "";
	private String senderEmail = "";
	private String recipientName = "";
	private String recipientEmail = "";
	
	private Recipient fromRecipient = null;
	private List<Recipient> toRecipients = null;
	private List<Recipient> ccRecipients = null;
	
	private String subject = "";
	private String body = "";
	private String tokenUrl = "";
	private String optoutUrl = "";
	private Locale locale = null;
	private boolean pdfAttached = false;
	private String pdfFileName = "";
	private ChangeType changeType;
	private MessageType messageType;
	private String recipeName = "";;
	private String userMessage = "";;
	private String originalEmail = "";
	private String userName = "";;
	private String userFirstName = "";;
	private Date messageDate = null;
	
	public EmailDetail() {}

	public EmailDetail(Locale locale) {
		this.locale = locale;
	}
	
	public EmailDetail(String recipientName, String recipientEmail, Locale locale) {
		this.recipientName = recipientName;
		this.recipientEmail = recipientEmail;
		this.locale = locale;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getRecipientName() {
		return recipientName;
	}
	
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}
	
	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public Recipient getFromRecipient() {
		return fromRecipient;
	}

	public void setFromRecipient(Recipient fromRecipient) {
		this.fromRecipient = fromRecipient;
	}

	public List<Recipient> getToRecipients() {
		return toRecipients;
	}

	public void setToRecipients(List<Recipient> toRecipients) {
		this.toRecipients = toRecipients;
	}

	public List<Recipient> getCcRecipients() {
		return ccRecipients;
	}

	public void setCcRecipients(List<Recipient> ccRecipients) {
		this.ccRecipients = ccRecipients;
	}

	public void setRecipients(List<Recipient> recipients, RecipientType type) {
		if (type == RecipientType.TO)
			toRecipients = recipients;
		else
		if (type == RecipientType.CC)
			ccRecipients = recipients;		
	}

	public List<InternetAddress> getInternetAddresses(RecipientType type) {
		List<InternetAddress> addresses = new ArrayList<InternetAddress>();
		List<Recipient> recipients = null;
		if (type == RecipientType.TO)
			recipients = toRecipients;
		else
		if (type == RecipientType.CC)
			recipients = ccRecipients;
		if (recipients != null) {
			for (Recipient recip : recipients) {
				InternetAddress address = new InternetAddress();
				address.setAddress(recip.getEmail());
				if (recip.hasPersonal()) {
					try {
						address.setPersonal(recip.getName());
					} catch (UnsupportedEncodingException e) {}
				}
				addresses.add(address);
			}
		}
		return addresses;
	}
	
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getTokenUrl() {
		return this.tokenUrl;
	}
	
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getOptoutUrl() {
		return optoutUrl;
	}

	public void setOptoutUrl(String optoutUrl) {
		this.optoutUrl = optoutUrl;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public Locale getLocale() {
		return this.locale;
	}

	public boolean isPdfAttached() {
		return pdfAttached;
	}

	public void setPdfAttached(boolean pdfAttached) {
		this.pdfAttached = pdfAttached;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		//replace line breaks with html breaks
		String msg = userMessage.replaceAll("\n", "<br/>");
		this.userMessage = msg;
	}

	public String getOriginalEmail() {
		return originalEmail;
	}

	public void setOriginalEmail(String originalEmail) {
		this.originalEmail = originalEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}
	
	public String getEncodedUser() {
		return null;
	}
}
