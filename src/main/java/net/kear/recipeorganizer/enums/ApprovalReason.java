package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum ApprovalReason {
	//NOTE: each enum MUST have a corresponding entry in labels.properties and messages.properties, e.g., 
	//approvaladmin.copyright=Copyright and approvalmsg.copyright=Copyrighted recipes cannot be shared.
	//the label MUST be spelled exactly the same
	CATEGORY,
	COPYRIGHT,
	DUPLICATE,
	INAPPROPRIATE,
	INGREDIENT,
	INSTRUCTION,
	INVALID,
	MISSPELLING,
	OTHER;
	
	public static List<ApprovalReason> list() {
		return Arrays.asList(values());
	}
	
	public static ApprovalReason parse(String value) {
		ApprovalReason action = null;
		for(ApprovalReason act : list()) {
			if (act.name().equalsIgnoreCase(value)) {
				action = act;
				break;
			}
		}
		
		return action;
	}	
}