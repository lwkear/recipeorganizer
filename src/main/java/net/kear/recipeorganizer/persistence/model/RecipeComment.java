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
@Table(name = "RECIPE_COMMENTS")
public class RecipeComment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECIPE_COMMENTS_SEQ")
	@SequenceGenerator(name = "RECIPE_COMMENTS_SEQ", sequenceName = "RECIPE_COMMENTS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "USER_ID")
	@NotNull
	private long userId;

	@Column(name = "RECIPE_ID")
	@NotNull
	private long recipeId;
	
	@Column(name = "USER_COMMENT")
	@Size(max=500)	//500
	private String userComment;
	
	@Column(name = "FLAG")
	private int flag;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name = "DATE_ADDED", insertable=false, updatable=false)
	private Date dateAdded;
	
	public RecipeComment() {}

	public RecipeComment(long id, long userId, long recipeId, String userComment, int flag) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.userComment = userComment;
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

	public long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(long recipeId) {
		this.recipeId = recipeId;
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
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
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
		RecipeComment other = (RecipeComment) obj;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (id != other.id)
			return false;
		if (recipeId != other.recipeId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeComment [id=" + id 
				+ ", userId=" + userId 
				+ ", recipeId=" + recipeId 
				+ ", userComment=" + userComment 
				+ ", dateAdded=" + dateAdded 
				+ ", flag=" + flag + "]";
	}
}