package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum ApprovalStatus {
	//NOTE: the values are stored in the RECIPE table, so do not change existing values when adding a new status
	NOTREVIEWED(0),
	PENDING(1),
	APPROVED(2),
	PRIVATE(3),
	BLOCKED(4);
	
	private final int value;
	
	ApprovalStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static List<ApprovalStatus> list() {
		return Arrays.asList(values());
	}
	
	public static ApprovalStatus parse(String value) {
		ApprovalStatus status = null;
		for(ApprovalStatus stat : list()) {
			if (stat.name().equalsIgnoreCase(value)) {
				status = stat;
				break;
			}
		}
		
		return status;
	}	

	//the following added during attempt to get the HQL RecipeRepository.viewedRecipes query to work
	//appeared they were not required, but hesitant to remove them just yet
	/*public static ApprovalStatus getStatus(int value) {
	ApprovalStatus status = null;
		for(ApprovalStatus stat : list()) {
			if (stat.getValue() == value) {
				status = stat;
				break;
			}
		}
	
		return status;
	}*/

	/*public static ApprovalStatus fromValue(int value) {
		ApprovalStatus status = null;
		for(ApprovalStatus stat : list()) {
			if (stat.getValue() == value) {
				status = stat;
				break;
			}
		}
	
		return status;
	}*/
}