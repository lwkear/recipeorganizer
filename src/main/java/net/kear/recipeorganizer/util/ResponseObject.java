package net.kear.recipeorganizer.util;

import javax.servlet.http.HttpServletResponse;

public class ResponseObject {

	final static String success = "Success";
	
	String msg;
	int status;
	
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
}

