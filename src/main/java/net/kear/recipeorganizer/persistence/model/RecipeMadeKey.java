package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class RecipeMadeKey implements Serializable {

	private static final long serialVersionUID = 1L;	

	@Column(name = "USER_ID")
	@NotNull
	private long userId;

	@Column(name = "RECIPE_ID")
	@NotNull
	private long recipeId;

	public RecipeMadeKey() {}

	public RecipeMadeKey(long userId, long recipeId) {
		super();
		this.userId = userId;
		this.recipeId = recipeId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		RecipeMadeKey other = (RecipeMadeKey) obj;
		if (recipeId != other.recipeId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeMadeKey [userId=" + userId 
				+ ", recipeId=" + recipeId + "]";
	}
}
