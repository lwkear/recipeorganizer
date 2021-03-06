package net.kear.recipeorganizer.util;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

public class ResponseObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	final static String success = "Success";
	
	String msg;
	int status;
	Object result;
	
	public ResponseObject() {
		this.msg = success;
		this.status = HttpServletResponse.SC_OK;
	}

	public ResponseObject(String msg, int status) {
		this.msg = msg;
		this.status = status;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}

