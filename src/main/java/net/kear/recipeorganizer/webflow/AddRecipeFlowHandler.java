package net.kear.recipeorganizer.webflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

@Component
public class AddRecipeFlowHandler extends AbstractFlowHandler {

	private static final String DEFAULT_URL = "/home";
	
    @Override
    public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
    		HttpServletResponse response) {
    	return DEFAULT_URL;
    }

    @Override
    public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
		if (e instanceof NoSuchFlowExecutionException) {
		    return DEFAULT_URL;
		} 
		else {
		    throw e;
		}
    }	
}
