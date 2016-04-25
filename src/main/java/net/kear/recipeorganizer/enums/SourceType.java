package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum SourceType {
	//NOTE: the names of this enum are stored in the SOURCE table
	COOKBOOK,
	MAGAZINE,
	NEWSPAPER,
	PERSON,
	WEBSITE,
	OTHER,
	NONE;
	
	public static List<SourceType> list() {
		return Arrays.asList();
	}
	
	public static SourceType parse(String name) {
		SourceType sourceType = null;
		for(SourceType source : list()) {
			if (source.name().equalsIgnoreCase(name)) {
				sourceType = source;
				break;
			}
		}
		
		return sourceType;
	}	
}