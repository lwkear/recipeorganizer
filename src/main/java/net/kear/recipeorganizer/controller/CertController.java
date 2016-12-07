package net.kear.recipeorganizer.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.enums.CertMode;
import net.kear.recipeorganizer.persistence.dto.CertificateDto;
import net.kear.recipeorganizer.persistence.dto.DomainChallenge;
import net.kear.recipeorganizer.util.maint.MaintAware;

import org.apache.commons.lang.StringUtils;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CertController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	

	@Autowired
    public Environment env;
	@Autowired
	private MessageSource messages;

	private static final String LETSENCRYPT_STAGING = "acme://letsencrypt.org/staging";
	private static final String LETSENCRYPT_PRODUCTION = "acme://letsencrypt.org";
    private static final String ACCOUNT_KEY = "account.key";
    private static final String DOMAIN_KEY = "recipeorganizer.key";
    private static final String DOMAIN_CSR = "recipeorganizer.csr";
    private static final String DOMAIN_CERT = "recipeorganizer.crt";
    private static final String DOMAIN_CHAIN = "recipeorganizerchain.crt";
    private static final String DOMAIN = "recipeorganizer.net";
    private static final int DOMAIN_COUNT = 2;
    private static final String CONTACT = "mailto:lkear@recipeorganizer.net";
    private static final int KEY_SIZE = 2048;
    private File accountKeyFile = null;
    private File challengeFile = null;
    private File domainKeyFile = null;
    private File domainCsrFile = null;
    private File domainCertFile = null;
    private File domainCertChainFile = null;
    private KeyPair accountKeyPair = null;
    private KeyPair domainKeyPair = null;
    private Session session = null;
    private Registration registration = null;
    private URI tosAgreement = null;
    private Authorization authorization = null;
    private Http01Challenge challenge = null;
    private List<String> domains = null;

	@MaintAware
	@RequestMapping(value = "/admin/certificate", method = RequestMethod.GET)
	public String certificateMaint(Model model) {
		logger.info("admin/certificate GET");
		
		domains = new ArrayList<String>();
		domains.add(DOMAIN);
		domains.add("www."+DOMAIN);
		
		CertificateDto certificateDto = new CertificateDto();
		certificateDto.setMode(CertMode.TEST);
		getCertificateInfo(certificateDto);
		
		model.addAttribute("certificateDto", certificateDto);
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/changeMode", method = RequestMethod.POST)
	public String changeMode(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/changeMode POST");

		//reinitialize the dto
		CertMode mode = certificateDto.getMode();
		CertificateDto certDto = new CertificateDto();
		certDto.setMode(mode);
		
		getCertificateInfo(certDto);

		model.addAttribute("certificateDto", certDto);
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/genAccountKey", method = RequestMethod.POST)
	public String genAccountKey(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/genAccountKey POST");
		certificateDto.setErrorMsg("");

        String fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + ACCOUNT_KEY; 
        accountKeyFile = new File(fileName);
        accountKeyPair = generateKey(certificateDto, accountKeyFile);
			
		if (accountKeyPair != null) {
	        certificateDto.setAccountKey(true);
	        certificateDto.setAccountKeyFile(accountKeyFile.getPath());
		}
    
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/registerAccount", method = RequestMethod.POST)
	public String registerAccount(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/registerAccount POST");
		certificateDto.setErrorMsg("");

		if (registerAcct(certificateDto))
			getAgreement(certificateDto);		
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/acceptAgreement", method = RequestMethod.POST)
	public String acceptAgreement(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/acceptAgreement POST");
		certificateDto.setErrorMsg("");

		if (acceptTosAgreement(certificateDto))
			authorizeDomain(certificateDto);
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/testChallenge", method = RequestMethod.POST)
	public String testChallenge(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/testChallenge POST");
		certificateDto.setErrorMsg("");

		boolean rslt = httpChallenge(certificateDto, locale);
		
		if (rslt) {
			int ndx = certificateDto.getDomainNdx() + 1;
			if (ndx < DOMAIN_COUNT) {
				certificateDto.setDomainNdx(ndx);				
				authorizeDomain(certificateDto);
			}
		}
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/genDomainKey", method = RequestMethod.POST)
	public String genDomainKey(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/genDomainKey POST");
		certificateDto.setErrorMsg("");

		String fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_KEY; 
        domainKeyFile = new File(fileName);
        domainKeyPair = generateKey(certificateDto, domainKeyFile);
			
        if (domainKeyPair != null) {
	        certificateDto.setDomainKey(true);
	        certificateDto.setDomainKeyFile(domainKeyFile.getPath());
		}
			
		return "admin/certificate";
	}
	
	@MaintAware
	@RequestMapping(value = "/admin/getCertificate", method = RequestMethod.POST)
	public String getCertificate(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/getCertificate POST");
		certificateDto.setErrorMsg("");

		getCert(certificateDto);
		
		return "admin/certificate";
	}
	
	private void getCertificateInfo(CertificateDto certificateDto) {
		logger.info("getCertificateInfo mode=" + certificateDto.getMode().name());
	
		certificateDto.setAccountKey(false);
		certificateDto.setRegistered(false);
		certificateDto.setAgreement(false);
		certificateDto.setCertificate(false);

		DomainChallenge domainChallenge = new DomainChallenge();
		domainChallenge.setChallenged(false);
		domainChallenge.setDomain(domains.get(0));
		certificateDto.getDomainChallengeList().add(domainChallenge);
		domainChallenge = new DomainChallenge();
		domainChallenge.setChallenged(false);
		domainChallenge.setDomain(domains.get(1));
		certificateDto.getDomainChallengeList().add(domainChallenge);
		certificateDto.setDomainNdx(0);
		certificateDto.setDomainCount(DOMAIN_COUNT);
		
        String fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + ACCOUNT_KEY; 
        accountKeyFile = new File(fileName);
        accountKeyPair = getKey(certificateDto, accountKeyFile);
        if (accountKeyPair == null) {
			certificateDto.setAccountKey(false);
			return;
		}
		
        certificateDto.setAccountKey(true);
        certificateDto.setAccountKeyFile(accountKeyFile.getPath());
		
        if (registerAcct(certificateDto))
        	getAgreement(certificateDto);

        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_KEY; 
        domainKeyFile = new File(fileName);
        domainKeyPair = getKey(certificateDto, domainKeyFile);
        if (domainKeyPair == null) {
			certificateDto.setDomainKey(false);
			return;
		}

        certificateDto.setDomainKey(true);
        certificateDto.setDomainKeyFile(domainKeyFile.getPath());
        
        if (existCertificates(certificateDto))
        	certificateDto.setCertificate(false);
	}
	
	private KeyPair getKey(CertificateDto certificateDto, File keyFile) {
        KeyPair keyPair = null;
		
        if (keyFile.exists()) {
			try {
				FileReader fr = new FileReader(keyFile);
				keyPair = KeyPairUtils.readKeyPair(fr);
	        } catch (Exception ex) {
	        	logger.error("getKey exception: " + ex.getMessage());
	        	certificateDto.setErrorMsg(ex.getMessage());
	        }
        }
                
        return keyPair;
	}
	
	private KeyPair generateKey(CertificateDto certificateDto, File keyFile) {
		KeyPair keyPair = null;
        try {
			keyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
			FileWriter fw = new FileWriter(keyFile);
			KeyPairUtils.writeKeyPair(keyPair, fw);
        } catch (Exception ex) {
        	logger.error("accountKey exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        }
        
        return keyPair;
	}

	private boolean registerAcct(CertificateDto certificateDto) {
        
		if (certificateDto.getMode() == CertMode.PROD)
			session = new Session(LETSENCRYPT_PRODUCTION, accountKeyPair);
		else
			session = new Session(LETSENCRYPT_STAGING, accountKeyPair);

        try {
        	registration = new RegistrationBuilder().addContact(CONTACT).create(session);
            logger.info("Registered a new user, URI: " + registration.getLocation());
        } catch (AcmeConflictException ex) {
        	registration = Registration.bind(session, ex.getLocation());
            logger.info("Account does already exist, URI: " + registration.getLocation());        	
        } catch (AcmeException ex) {
        	logger.error("registration exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
		}

        certificateDto.setRegistered(true);
        certificateDto.setRegistrationUri(registration.getLocation());
        return true;
	}
	
	private void getAgreement(CertificateDto certificateDto) {
        
        tosAgreement = registration.getAgreement();
        logger.info("Terms of Service: " + tosAgreement);
        
        certificateDto.setAgreementUri(tosAgreement);
 	}

	private boolean acceptTosAgreement(CertificateDto certificateDto) {
		
        try {
			registration.modify().setAgreement(tosAgreement).commit();
		} catch (AcmeException ex) {
        	logger.error("agreement exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
		}
        
        certificateDto.setAgreement(true);
        
        return true;
	}
	
	private void authorizeDomain(CertificateDto certificateDto) {
		
		int ndx = certificateDto.getDomainNdx();
		DomainChallenge domainChallenge = certificateDto.getDomainChallengeList().get(ndx); 
		
        try {
        	authorization = registration.authorizeDomain(domainChallenge.getDomain());
        } catch (AcmeUnauthorizedException ex) {
        	logger.error("unauthorized exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        } catch (AcmeException ex) {
	    	logger.error("authorize domain exception: " + ex.getMessage());
	    	certificateDto.setErrorMsg(ex.getMessage());
	    	return;
        }

        challenge = authorization.findChallenge(Http01Challenge.TYPE);
        if (challenge == null) {
        	String err = "No " + Http01Challenge.TYPE + " challenge found, don't know what to do...";
            logger.error(err);
            certificateDto.setErrorMsg(err);
            return;
        }
 
		String fileName = env.getProperty("file.directory.letsencrypt") + challenge.getToken(); 
		challengeFile = new File(fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(challengeFile);
			fw.write(challenge.getAuthorization());
			fw.close();
        } catch (Exception ex) {
        	logger.error("challengeFile exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		challengeFile.setReadable(true, false);
        URI challengeUri = URI.create("http://" + DOMAIN + "/.well-known/acme-challenge/");
        domainChallenge.setChallengeUri(challengeUri);
        domainChallenge.setChallengeFile(fileName);
        domainChallenge.setChallenged(false);
        certificateDto.getDomainChallengeList().set(ndx, domainChallenge);
	}

	private boolean httpChallenge(CertificateDto certificateDto, Locale locale) {

        try {
			challenge.trigger();
		} catch (AcmeException ex) {
        	logger.error("challenge trigger: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
		}

        //wait for the challenge to complete
        int attempts = 10;
        while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
            if (challenge.getStatus() == Status.INVALID) {
            	Object[] obj = new String[1];
            	obj[0] = challenge.getStatus().name();
            	String msg = messages.getMessage("certificate.challenge.status", obj, "", locale); 
            	logger.error(msg);
            	certificateDto.setErrorMsg(msg);
                return false;
            }
            
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException ex) {
            	logger.error("interrupted challenge", ex);
            	certificateDto.setErrorMsg(ex.getMessage());
            }
            
            try {
				challenge.update();
			} catch (AcmeException ex) {
	        	logger.error("update challenge: " + ex.getMessage());
	        	certificateDto.setErrorMsg(ex.getMessage());
			}
        }
        
        if (challenge.getStatus() != Status.VALID) {
        	Object[] obj = new String[1];
        	obj[0] = challenge.getStatus().name();
        	String msg = messages.getMessage("certificate.challenge.status", obj, "", locale); 
        	logger.error(msg);
        	certificateDto.setErrorMsg(msg);
            return false;
        }
        
        int ndx = certificateDto.getDomainNdx();
        certificateDto.getDomainChallengeList().get(ndx).setChallenged(true);
        return true;
	}

	private void getCert(CertificateDto certificateDto) {

        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomains(domains);
        try {
			csrb.sign(domainKeyPair);
		} catch (IOException ex) {
        	logger.error("domain CSR sign exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}

        String fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CSR; 
        domainCsrFile = new File(fileName);
        
		try {
			Writer out = new FileWriter(domainCsrFile);
			csrb.write(out);
			out.close();
        } catch (Exception ex) {
        	logger.error("domain CSR file exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		domainCsrFile.setReadable(true, false);
		certificateDto.setDomainCsrFile(domainCsrFile.getPath());
        
        Certificate certificate;
		try {
			certificate = registration.requestCertificate(csrb.getEncoded());
		} catch (AcmeException | IOException ex) {
        	logger.error("request certificate exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}

		logger.info("Success! The certificate has been generated!");
        logger.info("Certificate URI: " + certificate.getLocation());

        //download the certificate
        X509Certificate cert = null;
		try {
			cert = certificate.download();
		} catch (AcmeException ex) {
        	logger.error("cert download exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}

        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CERT; 
        domainCertFile = new File(fileName);

		try {
			FileWriter fw = new FileWriter(domainCertFile);
			CertificateUtils.writeX509Certificate(cert, fw);
			fw.close();
        } catch (Exception ex) {
        	logger.error("domain cert exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		domainCertFile.setReadable(true, false);
		certificateDto.setDomainCertFile(domainCertFile.getPath());
        
        //download the certificate chain
        X509Certificate[] chain = null; 
		try {
			chain = certificate.downloadChain();
		} catch (AcmeException ex) {
        	logger.error("cert chain download exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}

        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CHAIN; 
        domainCertChainFile = new File(fileName);
		
		try {
			FileWriter fw = new FileWriter(domainCertChainFile);
			CertificateUtils.writeX509CertificateChain(chain, fw);
			fw.close();
        } catch (Exception ex) {
        	logger.error("domain cert chain exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		domainCertChainFile.setReadable(true, false);
		certificateDto.setDomainCertChainFile(domainCertChainFile.getPath());
		certificateDto.setCertificate(true);
	}
	
	private boolean existCertificates(CertificateDto certificateDto) {
		
		int filecount = 0;
		
        String fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CSR; 
        domainCsrFile = new File(fileName);
        if (domainCsrFile.exists()) {
        	certificateDto.setDomainCsrFile(domainCsrFile.getPath());
        	filecount++;
        }        	
		
        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CERT; 
        domainCertFile = new File(fileName);
        if (domainCertFile.exists()) {
        	certificateDto.setDomainCertFile(domainCertFile.getPath());
        	filecount++;
        }
		
        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CHAIN; 
        domainCertChainFile = new File(fileName);
        if (domainCertFile.exists()) {
        	certificateDto.setDomainCertChainFile(domainCertChainFile.getPath());
        	filecount++;
        }
        
        return (filecount == 3 ? true : false);
	}
}