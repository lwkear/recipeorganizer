package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum UserAge {
	UAUNDER18(0,"< 18"),
	UA18TO30(1,"18-30"),
	UA31TO50(2,"31-50"),
	UA51TO70(3,"51-70"),
	UAOVER70(4,"> 70"),
	UANEVERMIND(5,"");
	
	private int value;
	private String description;
	
	UserAge(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public static List<UserAge> list() {
		return Arrays.asList(values());
	}
}