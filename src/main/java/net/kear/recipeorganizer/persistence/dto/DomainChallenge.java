package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.net.URI;

public class DomainChallenge implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean challenged;
	private URI challengeUri;
	private String challengeFile;
	private String domain;
	
	public boolean isChallenged() {
		return challenged;
	}

	public void setChallenged(boolean challenged) {
		this.challenged = challenged;
	}

	public URI getChallengeUri() {
		return challengeUri;
	}

	public void setChallengeUri(URI challengeUri) {
		this.challengeUri = challengeUri;
	}

	public String getChallengeFile() {
		return challengeFile;
	}

	public void setChallengeFile(String challengeFile) {
		this.challengeFile = challengeFile;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}	
}