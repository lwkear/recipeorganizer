package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum AudioType {
	INGREDIENTS,
	INSTRUCTIONS,
	NOTES,
	PRIVATENOTES;
	
	public static List<AudioType> list() {
		return Arrays.asList();
	}
	
	public static AudioType parse(String name) {
		AudioType sourceType = null;
		for(AudioType source : list()) {
			if (source.name().equalsIgnoreCase(name)) {
				sourceType = source;
				break;
			}
		}
		
		return sourceType;
	}
	
	public String getAudioType() {
		return this.name();
	}
}