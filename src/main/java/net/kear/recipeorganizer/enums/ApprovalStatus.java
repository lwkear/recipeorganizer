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
	
	private int value;
	
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
}