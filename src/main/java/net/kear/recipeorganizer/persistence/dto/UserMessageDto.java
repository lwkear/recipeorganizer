package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class UserMessageDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long toUserId;
	private long fromUserId;
	private String fromFirstName;
	private String fromLastName;
	private String fromEmail;
	private String subject;
	private String message;
	private String htmlMessage;
	private boolean viewed;
	//Note: recipeId needs to be a Long instead of a long since some messages may not be associated with a recipe and this field may be null
	private Long recipeId;
	private String recipeName;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date dateSent;
	
	public UserMessageDto() {}
	
	public UserMessageDto(long id, long toUserId, long fromUserId, String fromFirstName, String fromLastName, String fromEmail, String subject, String message,  
			String htmlMessage, boolean viewed, long recipeId, String recipeName, Date dateSent) {
		super();
		this.id = id;
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.fromFirstName = fromFirstName;
		this.fromLastName = fromLastName;
		this.fromEmail = fromEmail;
		this.subject = subject;
		this.message = message;
		this.htmlMessage = htmlMessage;
		this.viewed = viewed;
		this.recipeId = recipeId;
		this.recipeName = recipeName;
		this.dateSent = dateSent;		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}

	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public String getFromFirstName() {
		return fromFirstName;
	}

	public void setFromFirstName(String fromFirstName) {
		this.fromFirstName = fromFirstName;
	}

	public String getFromLastName() {
		return fromLastName;
	}

	public void setFromLastName(String fromLastName) {
		this.fromLastName = fromLastName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}

	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}
	
	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateSent == null) ? 0 : dateSent.hashCode());
		result = prime * result + (int) (toUserId ^ (toUserId >>> 32));
		result = prime * result + (int) (fromUserId ^ (fromUserId >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserMessageDto other = (UserMessageDto) obj;
		if (dateSent == null) {
			if (other.dateSent != null)
				return false;
		} else if (!dateSent.equals(other.dateSent))
			return false;
		if (toUserId != other.toUserId)
			return false;
		if (fromUserId != other.fromUserId)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MessageDto [id=" + id 
				+ ", toUserId=" + toUserId
				+ ", fromUserId=" + fromUserId 
				+ ", fromFirstName=" + fromFirstName 
				+ ", fromLastName=" + fromLastName
				+ ", fromEmail=" + fromEmail
				+ ", subject=" + subject
				+ ", message=" + message
				+ ", htmlMessage=" + htmlMessage
				+ ", viewed=" + viewed
				+ ", recipeId=" + recipeId
				+ ", recipeName=" + recipeName				
				+ ", dateSent=" + dateSent + "]";
	}
}
