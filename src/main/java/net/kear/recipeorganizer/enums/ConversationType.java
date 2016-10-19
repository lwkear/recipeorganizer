package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum ConversationType {
	GREETING,
	NOTTODAY,
	WHICHPART,
	ELEMENT_PLAY,
	ELEMENT_NA,
	ELEMENT_BAD,
	INGREDSET,
	INGREDSET_MATCH,
	INGREDSET_BAD,
	INGREDSET_TRYAGAIN,
	INSTRUCTSET,
	INSTRUCTSET_MATCH,
	INSTRUCTSET_BAD,
	INSTRUCTSET_TRYAGAIN,
	NOTUNDERSTAND;	
	
	public static List<ConversationType> list() {
		return Arrays.asList();
	}
	
	public static ConversationType parse(String name) {
		ConversationType sourceType = null;
		for(ConversationType source : list()) {
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