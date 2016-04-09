package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Arrays;

import net.kear.recipeorganizer.enums.ApprovalAction;
import net.kear.recipeorganizer.enums.ApprovalReason;

public class RecipeMessageDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long toUserId;
	private long recipeId;
	private ApprovalAction action;
	private ApprovalReason[] reasons;
	private String message;
	
	public RecipeMessageDto() {}

	public RecipeMessageDto(long toUserId, long recipeId, ApprovalAction action, ApprovalReason[] reasons, String message) {
		super();
		this.toUserId = toUserId;
		this.recipeId = recipeId;
		this.action = action;
		this.reasons = reasons;
		this.message = message;
	}

	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(long recipeId) {
		this.recipeId = recipeId;
	}

	public ApprovalAction getAction() {
		return action;
	}

	public void setAction(ApprovalAction action) {
		this.action = action;
	}

	public ApprovalReason[] getReasons() {
		return reasons;
	}

	public void setReasons(ApprovalReason[] reasons) {
		this.reasons = reasons;
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
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
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
		RecipeMessageDto other = (RecipeMessageDto) obj;
		if (action != other.action)
			return false;
		if (recipeId != other.recipeId)
			return false;
		if (toUserId != other.toUserId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeMessageDto [toUserId=" + toUserId 
				+ ", recipeId=" + recipeId 
				+ ", action=" + action 
				+ ", reasons=" + Arrays.toString(reasons) 
				+ ", message=" + message + "]";
	}
}
