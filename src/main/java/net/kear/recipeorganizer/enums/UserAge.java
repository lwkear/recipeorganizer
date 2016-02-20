package net.kear.recipeorganizer.enums;

public enum UserAge  {

	UAUNDER18(0),
	UA18TO30(1),
	UA31TO50(2),
	UA51TO70(3),
	UAOVER70(4),
	UANEVERMIND(5);
	
	private final int val;
	
	UserAge(int val) {
		this.val = val;
	}
	
	public int getValue() {
		return val;
	}
	
	public static UserAge fromInt(int i) {
        for (UserAge ua : UserAge.values()) {
            if (ua.getValue() == i) { 
            	return ua; 
            }
        }
        return null;
    }
}
