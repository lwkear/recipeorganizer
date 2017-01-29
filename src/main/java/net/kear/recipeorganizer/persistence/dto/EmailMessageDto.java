package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.List;

import net.kear.recipeorganizer.util.email.Recipient;

public class EmailMessageDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private List<Recipient> from;
	private List<Recipient> to;
	private List<Recipient> cc;
	private String subject;
	private String sentDate;
	private String content;
	private String message;	
	
	public EmailMessageDto() {}

	public EmailMessageDto(List<Recipient> from, List<Recipient> to, List<Recipient> cc, String subject, String sentDate, String content, String message) {
		super();
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.sentDate = sentDate;
		this.content = content;
		this.message = message;
	}

	public List<Recipient> getFrom() {
		return from;
	}

	public void setFrom(List<Recipient> from) {
		this.from = from;
	}

	public List<Recipient> getTo() {
		return to;
	}

	public void setTo(List<Recipient> to) {
		this.to = to;
	}

	public List<Recipient> getCc() {
		return cc;
	}

	public void setCc(List<Recipient> cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((sentDate == null) ? 0 : sentDate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		EmailMessageDto other = (EmailMessageDto) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (sentDate == null) {
			if (other.sentDate != null)
				return false;
		} else if (!sentDate.equals(other.sentDate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmailMessageDto [from=" + from 
				+ ", to=" + to 
				+ ", cc=" + cc 
				+ ", subject=" + subject 
				+ ", sentDate=" + sentDate 
				+ ", content=" + content 
				+ ", message=" + message + "]";
	}	
}
