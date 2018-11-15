package net.kear.recipeorganizer.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
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
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	private static final String LETSENCRYPT_STAGING = "acme://letsencrypt.org/staging";
	//private static final String LETSENCRYPT_STAGING = "https://acme-staging-v02.api.letsencrypt.org/directory";
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
    private Account account= null;
	private Order order = null;
    private URI tosAgreement = null;
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
	@RequestMapping(value = "/admin/acceptAgreement", method = RequestMethod.POST)
	public String acceptAgreement(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/acceptAgreement POST");
		certificateDto.setErrorMsg("");

		acceptTosAgreement(certificateDto);
		certificateDto.setRegistered(false);
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/registerAccount", method = RequestMethod.POST)
	public String registerAccount(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/registerAccount POST");
		certificateDto.setErrorMsg("");

		registerAcct(certificateDto);					
		
		return "admin/certificate";
	}

	@MaintAware
	@RequestMapping(value = "/admin/authorizeDomains", method = RequestMethod.POST)
	public String authorizeDomains(Model model, @ModelAttribute CertificateDto certificateDto, Locale locale) {
		logger.info("admin/authorizeDomains POST");
		certificateDto.setErrorMsg("");
		
		getAuthorization(certificateDto, locale);
		
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
		certificateDto.setAgreement(false);
		certificateDto.setRegistered(false);
		certificateDto.setAuthorized(false);
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
        
		getAgreement(certificateDto);
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

	private void getAgreement(CertificateDto certificateDto) {
        
		if (certificateDto.getMode() == CertMode.PROD)
			session = new Session(LETSENCRYPT_PRODUCTION);
		else
			session = new Session(LETSENCRYPT_STAGING);
		
		try {
			tosAgreement = session.getMetadata().getTermsOfService();
		} catch (AcmeException ex) {
        	logger.error("tos exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}
        logger.info("Terms of Service: " + tosAgreement);
        
        certificateDto.setAgreementUri(tosAgreement);
        certificateDto.setAgreement(false);
 	}

	private void acceptTosAgreement(CertificateDto certificateDto) {
		
        certificateDto.setAgreement(true);
        certificateDto.setRegistered(false);
	}
	
	private void registerAcct(CertificateDto certificateDto) {

		if (session == null) {
        	String err = "registerAcct problem: session is null";
            logger.error(err);
            certificateDto.setErrorMsg(err);
        	return;
		}
		
        try {
        	account = new AccountBuilder().addContact(CONTACT)
        								  .agreeToTermsOfService()
        								  .useKeyPair(accountKeyPair)
        								  .create(session);
        	logger.info("Registered a new user, URI: " + account.getLocation());
        } catch (AcmeException ex) {
        	logger.error("registration exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}        
    	
        certificateDto.setRegistered(true);
        URL urlLocation = account.getLocation();
        URI uriLocation = null;
		try {
			uriLocation = urlLocation.toURI();
		} catch (URISyntaxException e) {
			//do nothing
		}
        certificateDto.setRegistrationUri(uriLocation);
	}
	
	private void getAuthorization(CertificateDto certificateDto, Locale locale) {
		
		if (session == null) {
        	String err = "registerAcct problem: session is null";
            logger.error(err);
            certificateDto.setErrorMsg(err);
        	return;
		}
		
		if (account == null) {
        	String err = "registerAcct problem: session is null";
	        logger.error(err);
	        certificateDto.setErrorMsg(err);
	    	return;
		}
		
		try {
			order = account.newOrder().domains(domains).create();
			//order = account.newOrder().domain(DOMAIN).create();
			//order = account.newOrder().domain("www."+DOMAIN).create();
		} catch (AcmeException ex) {
        	logger.error("new order exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}
		
		String authDomains = "Authorized domains: ";
		int domainCount = 0;
		for (Authorization auth : order.getAuthorizations()) {
            if (authorizeDomain(auth, certificateDto, locale)) {
            	authDomains += auth.getIdentifier().getDomain() + " ";
            	domainCount++;
            }
        }
		
		if (domainCount == DOMAIN_COUNT) {
			certificateDto.setAuthorized(true);
			certificateDto.setAuthDomains(authDomains);
		}
		
		certificateDto.setAuthorized(true);
		//certificateDto.setAuthDomains("www."+DOMAIN);
		certificateDto.setAuthDomains(authDomains);
	}
	
	private boolean authorizeDomain(Authorization auth, CertificateDto certificateDto, Locale locale) {
		logger.info("Authorization for domain {}", auth.getIdentifier().getDomain());
		
		if (auth.getStatus() == Status.VALID) {
            return true;
        }
		
		Http01Challenge challenge = null;
		challenge = httpChallenge(auth, certificateDto, locale);
		if (challenge == null) {
        	return false;
		}
		
		if (challenge.getStatus() == Status.VALID) {
            return true;
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
        	return false;
        }
		
		logger.info("Created challenge file: {}", fileName);
		
		challengeFile.setReadable(true, false);
		
		try {
			challenge.trigger();
		} catch (AcmeException ex) {
        	logger.error("challenge exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
		}
		
		try {
            int attempts = 10;
            while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
                if (challenge.getStatus() == Status.INVALID) {
                    throw new AcmeException("Challenge failed... Giving up.");
                }

                Thread.sleep(3000L);
                challenge.update();
            }
		} catch (AcmeException ex) {
        	logger.error("challenge exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
		} catch (InterruptedException ex) {
        	logger.error("challenge exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return false;
        }
		
		if (challenge.getStatus() != Status.VALID) {
        	String err = "Failed to pass the challenge for domain " + auth.getIdentifier().getDomain() + ", ... Giving up.";
            logger.error(err);
            certificateDto.setErrorMsg(err);
        	return false;
        }
		
		return true;
	}

	private Http01Challenge httpChallenge(Authorization auth, CertificateDto certificateDto, Locale locale) {
	
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
	    if (challenge == null) {
	    	String msg = "Found no " + Http01Challenge.TYPE + " challenge, don't know what to do...";
        	logger.error(msg);
        	certificateDto.setErrorMsg(msg);
            return null;
	    }
	    
	    return challenge;
	}

	private void getCert(CertificateDto certificateDto) {

        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomains(domains);
        //csrb.addDomain(domains.get(1));
        //csrb.addDomain("www."+DOMAIN);
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
		
		try {
			order.execute(csrb.getEncoded());
		} catch (AcmeException ex) {
        	logger.error("order execute exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		} catch (IOException ex) {
        	logger.error("order execute exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		}
		
		try {
            int attempts = 10;
            while (order.getStatus() != Status.VALID && attempts-- > 0) {
                if (order.getStatus() == Status.INVALID) {
                    throw new AcmeException("Execute order failed... Giving up.");
                }

                Thread.sleep(3000L);
                order.update();
            }
		} catch (AcmeException ex) {
        	logger.error("order exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
		} catch (InterruptedException ex) {
        	logger.error("order exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		Certificate certificate = order.getCertificate();

        fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CERT; 
        domainCertFile = new File(fileName);

		try {
			FileWriter fw = new FileWriter(domainCertFile);
			certificate.writeCertificate(fw);
			fw.close();
        } catch (Exception ex) {
        	logger.error("domain cert exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		domainCertFile.setReadable(true, false);
		certificateDto.setDomainCertFile(domainCertFile.getPath());
		certificateDto.setCertificate(true);
		
        /*fileName = env.getProperty("file.directory.ssl." + StringUtils.lowerCase(certificateDto.getMode().name())) + DOMAIN_CHAIN; 
        domainCertChainFile = new File(fileName);
		
		try {
			FileWriter fw = new FileWriter(domainCertChainFile);
			certificate.writeCertificate(fw);
			fw.close();
        } catch (Exception ex) {
        	logger.error("domain cert chain exception: " + ex.getMessage());
        	certificateDto.setErrorMsg(ex.getMessage());
        	return;
        }
		
		domainCertChainFile.setReadable(true, false);
		certificateDto.setDomainCertChainFile(domainCertChainFile.getPath());
		certificateDto.setCertificate(true);*/
		
		/* pervious version
		int ndx = certificateDto.getDomainNdx();
		domainCsrFile.setReadable(true, false);
		certificateDto.setDomainCsrFile(domainCsrFile.getPath());
        
		Order order = account.newOrder().domain(certificateDto.getDomainChallengeList().get(ndx).getDomain()).create();
        Certificate certificate;
		try {
			certificate = account.getOrders()
					registration.requestCertificate(csrb.getEncoded());
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
		certificateDto.setCertificate(true);*/
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