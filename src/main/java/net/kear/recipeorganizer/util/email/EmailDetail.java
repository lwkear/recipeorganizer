package net.kear.recipeorganizer.util.email;

import java.util.Date;
import java.util.Locale;

import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.util.email.AccountChangeEmail.ChangeType;

public class EmailDetail {

	private String senderName = "";
	private String senderEmail = "";
	private String recipientName = "";
	private String recipientEmail = "";
	private String subject = "";
	private String body = "";
	private String tokenUrl = "";
	private String optoutUrl = "";
	private Locale locale = null;
	private boolean pdfAttached = false;
	private String pdfFileName = "";
	private ChangeType changeType;
	private MessageType messageType;
	private String recipeName;
	private String userMessage;
	private String userName;
	private String userFirstName;
	private Date messageDate;
	
	public EmailDetail() {}
	
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
		this.userMessage = userMessage;
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
