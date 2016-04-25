package net.kear.recipeorganizer.enums;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.util.TagUtils;

//NOTE: much of this was lifted from Spring's MessageTag implementation

public class ApprovalStatusTag extends RequestContextAwareTag {

	private static final long serialVersionUID = 1L;
	
	private String text;
	private String var;
	private String scope = TagUtils.SCOPE_PAGE;
	private ApprovalStatus status;
	
	public ApprovalStatusTag() {}
	
	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus approvalStatus) {
		this.status = approvalStatus;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	@Override
	protected int doStartTagInternal() throws Exception {
		return EVAL_BODY_INCLUDE;	
	}

	@Override
	public int doEndTag() throws JspException {

		try {
			// Resolve the unescaped message.
			String msg = resolveMessage();

			// Expose as variable, if demanded, else write to the page.
			if (this.var != null) {
				pageContext.setAttribute(this.var, msg, TagUtils.getScope(this.scope));
			}
			else {
				writeMessage(msg);
			}

			return EVAL_PAGE;
		}
		catch (IOException ex) {
			throw new JspTagException(ex.getMessage(), ex);
		}
		catch (NoSuchMessageException ex) {
			throw new JspTagException(getNoSuchMessageExceptionDescription(ex));
		}
	}

	protected String resolveMessage() throws JspException, NoSuchMessageException {
		MessageSource messageSource = getMessageSource();
		if (messageSource == null) {
			throw new JspTagException("No corresponding MessageSource found");
		}

		if (this.status != null || this.text != null) {

			String code = "approvalstatus." + status.name().toLowerCase();
			
			if (this.text != null) {
				// We have a fallback text to consider.
				return messageSource.getMessage(code, null, this.text, getRequestContext().getLocale());
			}
			else {
				// We have no fallback text to consider.
				return messageSource.getMessage(code, null, getRequestContext().getLocale());
			}
		}

		// All we have is a specified literal text.
		return this.text;
	}
	
	protected MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}
	
	protected Locale getLocale() {
		return getRequestContext().getLocale();
	}

	protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
		return ex.getMessage();
	}

	protected void writeMessage(String msg) throws IOException {
		pageContext.getOut().write(String.valueOf(msg));
	}
}