package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class CommentDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long id;
	private long userId;
	private String firstName;
	private String lastName;
	private String avatar;
	private String userComment;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date dateAdded;
	private int flag;
	
	public CommentDto() {}

	public CommentDto(long id, long userId, String firstName, String lastName, String avatar, String userComment, Date dateAdded, int flag) {
		super();
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.avatar = avatar;
		this.userComment = userComment;
		this.dateAdded = dateAdded;
		this.flag = flag;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		CommentDto other = (CommentDto) obj;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (id != other.id)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CommentDto [id=" + id 
				+ ", userId=" + userId 
				+ ", firstName=" + firstName 
				+ ", lastName=" + lastName 
				+ ", avatar=" + avatar 
				+ ", userComment=" + userComment 
				+ ", dateAdded=" + dateAdded 
				+ ", flag=" + flag + "]";
	}
}
