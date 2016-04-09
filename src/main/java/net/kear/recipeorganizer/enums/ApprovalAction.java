package net.kear.recipeorganizer.enums;

import java.util.Arrays;
import java.util.List;

public enum ApprovalAction {
	//NOTE: each enum MUST have a corresponding entry in labels.properties, e.g., approvaladmin.delete=Delete
	//the label MUST be spelled exactly the same 
	APPROVE,
	PENDING,	
	BLOCK,
	DELETE;
	
	public static List<ApprovalAction> list() {
		return Arrays.asList(values());
	}
	
	public static ApprovalAction parse(String value) {
		ApprovalAction action = null;
		for(ApprovalAction act : list()) {
			if (act.name().equalsIgnoreCase(value)) {
				action = act;
				break;
			}
		}
		
		return action;
	}	
}