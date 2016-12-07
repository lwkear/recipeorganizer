package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.enums.CertMode;

public class CertificateDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private CertMode mode;
	private boolean accountKey;
	private String accountKeyFile;
	private boolean domainKey;
	private String domainKeyFile;
	private boolean registered;
	private URI registrationUri;
	private boolean agreement;
	private URI agreementUri;
	private boolean certificate;
    private String domainCsrFile;
    private String domainCertFile;
    private String domainCertChainFile;
	private String errorMsg;
	private int domainCount;
	private int domainNdx;
	private List<DomainChallenge> domainChallengeList = new AutoPopulatingList<DomainChallenge>(DomainChallenge.class);
 	
	public CertificateDto() {}

	public CertMode getMode() {
		return mode;
	}

	public void setMode(CertMode mode) {
		this.mode = mode;
	}

	public boolean isAccountKey() {
		return accountKey;
	}

	public void setAccountKey(boolean accountKey) {
		this.accountKey = accountKey;
	}

	public String getAccountKeyFile() {
		return accountKeyFile;
	}

	public void setAccountKeyFile(String accountKeyFile) {
		this.accountKeyFile = accountKeyFile;
	}

	public boolean isDomainKey() {
		return domainKey;
	}

	public void setDomainKey(boolean domainKey) {
		this.domainKey = domainKey;
	}

	public String getDomainKeyFile() {
		return domainKeyFile;
	}

	public void setDomainKeyFile(String domainKeyFile) {
		this.domainKeyFile = domainKeyFile;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public URI getRegistrationUri() {
		return registrationUri;
	}

	public void setRegistrationUri(URI registrationUri) {
		this.registrationUri = registrationUri;
	}

	public boolean isAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}

	public URI getAgreementUri() {
		return agreementUri;
	}

	public void setAgreementUri(URI agreementUri) {
		this.agreementUri = agreementUri;
	}

	public boolean isCertificate() {
		return certificate;
	}

	public void setCertificate(boolean certificate) {
		this.certificate = certificate;
	}

	public String getDomainCsrFile() {
		return domainCsrFile;
	}

	public void setDomainCsrFile(String domainCsrFile) {
		this.domainCsrFile = domainCsrFile;
	}

	public String getDomainCertFile() {
		return domainCertFile;
	}

	public void setDomainCertFile(String domainCertFile) {
		this.domainCertFile = domainCertFile;
	}

	public String getDomainCertChainFile() {
		return domainCertChainFile;
	}

	public void setDomainCertChainFile(String domainCertChainFile) {
		this.domainCertChainFile = domainCertChainFile;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getDomainCount() {
		return domainCount;
	}

	public void setDomainCount(int domainCount) {
		this.domainCount = domainCount;
	}
		
	public int getDomainNdx() {
		return domainNdx;
	}

	public void setDomainNdx(int domainNdx) {
		this.domainNdx = domainNdx;
	}

	public List<DomainChallenge> getDomainChallengeList() {
		return domainChallengeList;
	}
	
	public DomainChallenge getDomainChallenge(int ndx) {
		return this.domainChallengeList.get(ndx);
	}

	public void setDomainChallengeList(List<DomainChallenge> domainChallengeList) {
		this.domainChallengeList = domainChallengeList;
	}
	
	public void setDomainChallenge(int ndx, DomainChallenge domainChallenge) {
		this.domainChallengeList.set(ndx, domainChallenge);
	}
}