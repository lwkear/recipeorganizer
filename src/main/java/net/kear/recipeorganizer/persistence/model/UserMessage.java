package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "MESSAGE")
public class UserMessage implements Serializable {

	private static final long serialVersionUID = 1L;	

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_SEQ")
	@SequenceGenerator(name = "MESSAGE_SEQ", sequenceName = "MESSAGE_SEQ", allocationSize = 1)
	private long id;
	
	@Column(name = "FROM_USER_ID")
	@NotNull
	private long fromUserId;

	@Column(name = "TO_USER_ID")
	@NotNull
	private long toUserId;
	
	@Column(name = "MESSAGE")
	@Size(max=1000)	//1000
	private String message;

	@Column(name = "VIEWED")
	private boolean viewed;

	@Column(name = "RECIPE_ID")
	private Long recipeId;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_SENT", insertable=false, updatable=false)
	private Date dateSent;
	
	public UserMessage() {}

	public UserMessage(long fromUserId, long toUserId, String message, boolean viewed, Long recipeId) {
		super();
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.message = message;
		this.viewed = viewed;
		this.recipeId = recipeId;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
		result = prime * result + (int) (fromUserId ^ (fromUserId >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (toUserId ^ (toUserId >>> 32));
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
		UserMessage other = (UserMessage) obj;
		if (dateSent == null) {
			if (other.dateSent != null)
				return false;
		} else if (!dateSent.equals(other.dateSent))
			return false;
		if (fromUserId != other.fromUserId)
			return false;
		if (id != other.id)
			return false;
		if (toUserId != other.toUserId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [id=" + id
				+ ", fromUserId=" + fromUserId
				+ ", toUserId=" + toUserId
				+ ", message=" + message
				+ ", viewed=" + viewed
				+ ", recipeId=" + recipeId
				+ ", dateSent=" + dateSent + "]";
	}
}
