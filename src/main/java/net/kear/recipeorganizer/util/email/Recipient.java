package net.kear.recipeorganizer.util.email;

import java.io.UnsupportedEncodingException;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Recipient {

	private String name;
	private String email;
	private String full;
	private boolean hasName = true; 
	
	public Recipient() {}
	
	public Recipient(String name, String email, String full, boolean hasName) {
		super();
		this.name = name;
		this.email = email;
		this.full = full;
		this.hasName = hasName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFull() {
		return full;
	}

	public void setFull(String full) {
		this.full = full;
	}
	
	public boolean hasPersonal() {
		return hasName;
	}

	public void setHasName(boolean hasName) {
		this.hasName = hasName;
	}

	@JsonIgnore
	public InternetAddress getFromAddress() {
		InternetAddress address = new InternetAddress();
		address.setAddress(email);
		if (hasPersonal())
			try {
				address.setPersonal(name);
			} catch (UnsupportedEncodingException e) {}
		return address;
	}
	
	@JsonIgnore
	public InternetAddress getInternetAddress(RecipientType type) {
		InternetAddress address = new InternetAddress();
		address.setAddress(email);
		if (hasPersonal()) {
			try {
				address.setPersonal(name);
			} catch (UnsupportedEncodingException e) {}
		}
		return address;
	}
}
