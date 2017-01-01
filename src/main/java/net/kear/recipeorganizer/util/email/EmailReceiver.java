package net.kear.recipeorganizer.util.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AutoPopulatingList;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import net.kear.recipeorganizer.persistence.dto.EmailDto;

@Component
public class EmailReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Properties mailProperties = new Properties();
	private Session session = null;
	private IMAPStore store = null;
	private String protocol;
	private String host;
	private int port;
	private String username;
	private String password;
	private String defaultEncoding;
	private String emailDir;
	private static final Map<String, String> docTypes;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("doc","application/msword");
		map.put("pdf","application/octet-stream");
		map.put("txt","text/plain");
		map.put("xls","application/vnd.ms-excel");
		docTypes = Collections.unmodifiableMap(map);
	}
	private static final List<String> mimeTypes;
	static {
		List<String> list = new ArrayList<String>();
		list.add("image/*");
		list.add("application/octet-stream");
		list.add("application/vnd.ms-excel");
		list.add("application/msword");
		list.add("text/plain");
		mimeTypes = Collections.unmodifiableList(list);
	}	
	public EmailReceiver() {}

	public boolean connect() {
		logger.debug("connecting to " + mailProperties.getProperty("mail.imap.host"));
		session = Session.getInstance(mailProperties);
		store = null;
		
		try {
			store = (IMAPStore) session.getStore();
			store.connect(host, port, username, password);
			/*if (!store.hasCapability("IDLE")) {
				logger.debug("NO IDLE CAPABILITY!");
			}*/
			return true;
		} catch (MessagingException ex) {
			logger.debug(ex.getMessage());
		}
		
		return false;
	}
	
	public Properties getMailProperties() {
		return mailProperties;
	}

	public void setMailProperties(Properties mailProperties) {
		this.mailProperties = mailProperties;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	
	public String getEmailDir() {
		return emailDir;
	}

	public void setEmailDir(String emailDir) {
		this.emailDir = emailDir;
	}
	
	public boolean isValidDocType(String extension) {
		String ext = StringUtils.substring(extension, 0, 3);
		return (docTypes.containsKey(ext));
	}
	
	public String getDocContentType(String extension) {
		return (docTypes.get(extension));
	}

	public List<EmailDto> getMessages(String contextPath) {
		List<EmailDto> emails = new AutoPopulatingList<EmailDto>(EmailDto.class);
		Message[] messages = null;
		IMAPFolder folder = null;
		
		try {
			folder = getFolder("INBOX");
			if (folder == null)
				return emails;
			
			int count = folder.getMessageCount();
			logger.debug("msg count:" + count);
			folder.open(Folder.READ_WRITE);
			messages = folder.getMessages();
			for (Message msg : messages) {
				EmailDto email = new EmailDto();
				email.setMsgNum(msg.getMessageNumber());
				email.setSubject(msg.getSubject());
				email.setFrom(msg.getFrom());
				email.setTo(msg.getRecipients(RecipientType.TO));
				email.setCc(msg.getRecipients(RecipientType.CC));
				email.setBcc(msg.getRecipients(RecipientType.BCC));
				email.setSentDate(msg.getSentDate());
				email.setFlags(msg.getFlags());

				//check for attached or inline files; currently only handling images
				boolean hasAttach = hasFile(msg, Part.ATTACHMENT);
				String fileNames = "";
				if (hasAttach) {
					fileNames = getFileNames(msg, msg.getSentDate().getTime(), fileNames, Part.ATTACHMENT);
					email.setFileNames(fileNames);
				}
				email.setHasAttachment(hasAttach);
				boolean hasInline = hasFile(msg, Part.INLINE);
				fileNames = "";
				if (hasInline)
					fileNames = getFileNames(msg, msg.getSentDate().getTime(), fileNames, Part.INLINE);
				
				//fix the Outlook email issue with the menu and replace any inline file src 
				String text = fixContent(getText(msg), hasInline, fileNames, email.getSortDate(), contextPath);
				email.setContent(text);
				MimeMessage mime = (MimeMessage)msg;
				email.setMimeId(mime.getMessageID());
				emails.add(email);
				
				//if (msg.getMessageNumber() > 40)
				//	parseParts(msg.getMessageNumber(), msg.getSubject(), msg);
			}
			
            folder.close(false);
		} catch (MessagingException | IOException ex) {
			logger.debug(ex.getMessage());
		}		
		
		return emails;
	}
	
	private boolean isConnected() {
		if (session == null || store == null || !store.isConnected())
			return connect();
		return true;
	}
	
	private IMAPFolder getFolder(String folderName) {
		IMAPFolder folder = null;
		
		try {
			if (!isConnected())
				return folder;
			
			folder = (IMAPFolder) store.getFolder(folderName);
			if (folder.exists())
				return folder;
		} catch (MessagingException ex) {
			logger.debug(ex.getMessage());
		}		

		return folder;
	}

	private boolean hasFile(Part part, String disposition) throws MessagingException, IOException {
		String dispos = part.getDisposition();
		if (dispos == null)
			dispos = "n/a";

		if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
			String type = part.getContentType();
			if (isValidMimeType(type))
				return true;
			logger.debug("found invalid" + disposition + " file:" + type);
		}				
		
		if (part.isMimeType("multipart/*")) {
			Multipart parts = (Multipart) part.getContent();
			int numParts = parts.getCount();
			for (int i=0;i<numParts;i++) {
				Part p = parts.getBodyPart(i);
				dispos = p.getDisposition();
				if (dispos == null)
					dispos = "n/a";

				if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
					String type = p.getContentType();
					if (isValidMimeType(type))
						return true;
					logger.debug("found invalid" + disposition + " file:" + type);
				}				
				
				if (p.isMimeType("multipart/*")) {
					boolean foundFile = hasFile(p, disposition);
					if (foundFile)
						return true;
				}
			}
		}
		
		return false;
	}

	private String getFileNames(Part part, long id, String names, String disposition) throws MessagingException, IOException {
		String dispos = part.getDisposition();
		if (dispos == null)
			dispos = "n/a";

		if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
			String type = part.getContentType();
			if (isValidMimeType(type)) {
				MimeBodyPart bp = (MimeBodyPart)part;
				String fileName = bp.getFileName();
				String filePath = emailDir + id + "." + fileName;
				bp.saveFile(filePath);
				logger.debug("fileName:" + fileName);
				if (!StringUtils.isEmpty(names))
					fileName = "," + fileName;
				return names + fileName;
			}
		}				
		
		if (part.isMimeType("multipart/*")) {
			Multipart parts = (Multipart) part.getContent();
			int numParts = parts.getCount();
			for (int i=0;i<numParts;i++) {
				Part p = parts.getBodyPart(i);
				dispos = p.getDisposition();
				if (dispos == null)
					dispos = "n/a";

				if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
					String type = p.getContentType();
					if (isValidMimeType(type)) {
						MimeBodyPart bp = (MimeBodyPart)p;
						String fileName = bp.getFileName();
						String filePath = emailDir + id + "." + fileName;
						bp.saveFile(filePath);
						logger.debug("fileName:" + fileName);
						if (!StringUtils.isEmpty(names))
							fileName = "," + fileName;
						names = names + fileName;
					}
				}				
				
				if (p.isMimeType("multipart/*")) {
					names = names + getFileNames(p, id, names, disposition);
					
				}
			}
		}
		
		return names;
	}	

	/*getText lifted from http://www.oracle.com/technetwork/java/javamail/faq/index.html*/	
	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			int numParts = mp.getCount();
			String text = null;
			for (int i = 0; i < numParts; i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} 
				else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} 
				else {
					return getText(bp);
				}
			}
			return text;
		} 
		else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}
	
	private String fixContent(String content, boolean hasInline, String inlineFileNames, long id, String contextPath) {
		String text = content;
		
		if (StringUtils.isEmpty(text))
			return text;

		//Outlook html emails include inline styles which affect the entire page, specifically the menu bar;
		//restricting the <style> by adding "scoped" only works in Firefox
		/*if (StringUtils.contains(text, "<style>"))
			text = StringUtils.replace(text, "<style>", "<style scoped>");*/
		//alternate method is to remove the offending portion of the style
		if (StringUtils.contains(text, "a:link, "))
			text = StringUtils.replace(text, "a:link, ", "");
		if (StringUtils.contains(text, "a:visited, "))
			text = StringUtils.replace(text, "a:visited, ", "");
		
		if (hasInline) {
			if (StringUtils.isEmpty(inlineFileNames))
				return text;
			
			String[] names = inlineFileNames.split(",");
			
			int imgBegin = 0;
			int imgEnd = 0;
			int srcBegin= 0;
			int srcEnd= 0;
			String imgTag = "";
			String imgSrc = "";
			String newSrc = "";
			String newTag = "";
			
			imgBegin = StringUtils.indexOfIgnoreCase(text, "<img", imgBegin);
			while (imgBegin > 0) {
				imgEnd = StringUtils.indexOfIgnoreCase(text, ">", imgBegin);
				if (imgEnd < 0)
					break;
				imgTag = StringUtils.substring(text, imgBegin, imgEnd+1);
				
				for (int i=0;i<names.length;i++) {
					String fileName = names[i];
					if (StringUtils.contains(imgTag, fileName)) {
						srcBegin = StringUtils.indexOfIgnoreCase(imgTag, "src=\"", 0);
						if (srcBegin < 0)
							break;
						srcEnd = StringUtils.indexOfIgnoreCase(imgTag, "\"", srcBegin+5);
						if (srcEnd < 0)
							break;
						imgSrc = StringUtils.substring(imgTag, srcBegin, srcEnd+1);
						newSrc = String.format("src=\"%s/admin/attachment?id=%d&filename=%s\"", contextPath, id, fileName);
						newTag = StringUtils.replace(imgTag, imgSrc, newSrc);
						text = StringUtils.replace(text, imgTag, newTag);						
					}					
				}
				
				imgBegin = StringUtils.indexOfIgnoreCase(text, "<img", imgEnd);
			}
		}
		
		return text;
	}

	//this app only handles a small set of mime types
	private boolean isValidMimeType(String type) {
		String mimeType = StringUtils.substringBefore(type, ";");
		String wildcardType = StringUtils.substringBefore(mimeType, "/");
		wildcardType += "/*";
		return (mimeTypes.contains(mimeType.toLowerCase()) || mimeTypes.contains(wildcardType.toLowerCase()));
	}
	
	@SuppressWarnings("unused")
	private void parseParts(int num, String subj, Part part) throws MessagingException, IOException {
		String type = part.getContentType();
		String dispos = part.getDisposition();
		if (dispos == null)
			dispos = "n/a";
		String debug = String.format("msg# %d; subj: %s; content type: %s; disposition %s", num, subj, type, dispos);
		logger.debug(debug);
		
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++)
				parseParts(num, subj, mp.getBodyPart(i));
		}
	}

	@SuppressWarnings("unused")
	private boolean hasFileDebug(int num, int count, String subj, Part part, String disposition) throws MessagingException, IOException {
		String type = part.getContentType();
		String dispos = part.getDisposition();
		if (dispos == null)
			dispos = "n/a";

		String debug = String.format("msg# %d; count %d; subj: %s; content type: %s; disposition %s", num, count, subj, type, dispos);
		logger.debug(debug);

		if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
			if (part.isMimeType("image/*") || part.isMimeType("application/octet-stream"))
				return true;
		}				
		
		if (part.isMimeType("multipart/*")) {
			Multipart parts = (Multipart) part.getContent();
			int numParts = parts.getCount();
			for (int i=0;i<numParts;i++) {
				Part p = parts.getBodyPart(i);
				type = p.getContentType();
				dispos = p.getDisposition();
				if (dispos == null)
					dispos = "n/a";

				debug = String.format("msg# %d; count %d; subj: %s; content type: %s; disposition %s", num, count, subj, type, dispos);
				logger.debug(debug);
				
				if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
					if (p.isMimeType("image/*") || p.isMimeType("application/octet-stream"))
						return true;
				}				
				
				if (p.isMimeType("multipart/*")) {
					int n = count+1;
					boolean foundFile = hasFileDebug(num, n, subj, p, disposition);
					if (foundFile)
						return true;
				}
			}
		}
		
		return false;
	}
}
