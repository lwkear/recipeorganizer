package net.kear.recipeorganizer.persistence.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class CommentDto {

	private long id;
	private String firstName;
	private String lastName;
	private String avatar;
	private String userComment;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date dateAdded;
	
	public CommentDto() {}

	public CommentDto(long id, String firstName, String lastName, String avatar, String userComment, Date dateAdded) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.avatar = avatar;
		this.userComment = userComment;
		this.dateAdded = dateAdded;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	};
	
}
