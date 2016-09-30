package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import net.kear.recipeorganizer.enums.AudioType;
import net.kear.recipeorganizer.enums.ConversationType;

public class ConversationDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long userId;
	private long recipeId;
	private int section;
	private AudioType audioType;
	private ConversationType conversationType;
	
	public ConversationDto() {}

	public ConversationDto(long userId, long recipeId, int section, AudioType audioType, ConversationType conversationType) {
		super();
		this.userId = userId;
		this.recipeId = recipeId;
		this.section = section;
		this.audioType = audioType;
		this.conversationType = conversationType;
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

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public AudioType getAudioType() {
		return audioType;
	}

	public void setAudioType(AudioType audioType) {
		this.audioType = audioType;
	}

	public ConversationType getConversationType() {
		return conversationType;
	}

	public void setConversationType(ConversationType conversationType) {
		this.conversationType = conversationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((audioType == null) ? 0 : audioType.hashCode());
		result = prime * result + ((conversationType == null) ? 0 : conversationType.hashCode());
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
		result = prime * result + section;
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
		ConversationDto other = (ConversationDto) obj;
		if (audioType != other.audioType)
			return false;
		if (conversationType != other.conversationType)
			return false;
		if (recipeId != other.recipeId)
			return false;
		if (section != other.section)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConversationDto [userId=" + userId 
				+ ", recipeId=" + recipeId 
				+ ", section=" + section 
				+ ", audioType=" + audioType 
				+ ", conversationType=" + conversationType + "]";
	}	
}
