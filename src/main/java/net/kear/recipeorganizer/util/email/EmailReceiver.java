package net.kear.recipeorganizer.util.email;

import java.io.IOException;
import java.util.List;
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

	public List<EmailDto> getMessages(String contextPath) {
		List<EmailDto> emails = new AutoPopulatingList<EmailDto>(EmailDto.class);
		Message[] messages = null;
		IMAPFolder folder = null;
		//int cnt = 0;
		
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
				boolean hasAttach = hasFile(msg.getMessageNumber(), msg.getSubject(), msg, Part.ATTACHMENT);
				if (hasAttach)
					email.setFileNames(getFileNames(msg, Part.ATTACHMENT));
				email.setHasAttachment(hasAttach);
				boolean hasInline = hasFile(msg.getMessageNumber(), msg.getSubject(), msg, Part.INLINE);
				String inlineFileNames = "";
				if (hasInline)
					inlineFileNames = getFileNames(msg, Part.INLINE);
				String text = fixContent(getText(msg), hasInline, inlineFileNames, email.getSortDate(), contextPath);
				email.setContent(text);
				MimeMessage mime = (MimeMessage)msg;
				email.setMimeId(mime.getMessageID());
				emails.add(email);
				
				//parseParts(msg.getMessageNumber(), msg.getSubject(), msg);
				//cnt++;
				//if (cnt > 5)
				//	break;					
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

	private boolean hasFile(int num, String subj, Part part, String disposition) throws MessagingException, IOException {
		
		String type = part.getContentType();
		String dispos = part.getDisposition();
		if (dispos == null)
			dispos = "n/a";

		String debug = String.format("MainPart msg# %d; subj: %s; content type: %s; disposition %s", num, subj, type, dispos);
		//logger.debug(debug);
		
		boolean foundAttachment = false;
		
		if ((part.isMimeType("multipart/mixed")) || (part.isMimeType("multipart/related"))) {

			Multipart parts = (Multipart) part.getContent();
			int numParts = parts.getCount();
			for (int i=0;i<numParts;i++) {
				Part p = parts.getBodyPart(i);

				type = p.getContentType();
				dispos = p.getDisposition();
				if (dispos == null)
					dispos = "n/a";

				debug = String.format("BodyPart msg# %d; subj: %s; content type: %s; disposition %s", num, subj, type, dispos);
				//logger.debug(debug);
				
				if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
					debug = String.format("dispo %s: msg# %d; subj: %s; content type: %s; disposition %s", dispos, num, subj, type, dispos);
					logger.debug(debug);
					foundAttachment = true;
				}				
				
				if ((p.isMimeType("multipart/mixed")) || (p.isMimeType("multipart/related"))) {
					return hasFile(num, subj, p, disposition);
				}
			}
		}
		
		return foundAttachment;
	}

	//TODO: this may not account for nested parts with attachments
	private String getFileNames(Message msg, String disposition) throws MessagingException, IOException {
		String fileNames = "";
		String separator = "";
		Multipart parts = (Multipart) msg.getContent();
		int numParts = parts.getCount();
		for (int i=0;i<numParts;i++) {
			MimeBodyPart part = (MimeBodyPart)parts.getBodyPart(i);
			String dispos = part.getDisposition(); 
			if (dispos != null && dispos.equalsIgnoreCase(disposition)) {
				String fileName = part.getFileName();
				long id = msg.getSentDate().getTime();
				String filePath = emailDir + id + "." + fileName;
				fileNames += separator + fileName;
				separator = ",";
				part.saveFile(filePath);
			}
		}
		
		return fileNames;
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
		
		//email INLINE: <img id="Picture_x0020_1" src="cid:image001.jpg@01D25AC9.0AEF7FC0" width="853" height="640">
		//recipe example: <img class="img-responsive center-block" src="/recipeorganizer/recipe/photo?id=4&amp;filename=MacAndCheese.JPG" alt="No photo">
		//outlook inline: <img originalsrc="cid:edbae9bb-12c7-4ab1-ba6f-1c9b5fadd597" data-custom="AQMkADAwATM0MDAAMS1hZGQAZS1hNjc1LTAwAi0wMAoARgAAA9Kr6IbgTyBAiSRBdO1YQT0HAKhZcJx8%2BFJCmhIVJmHCh0cAAAIBDAAAAKhZcJx8%2BFJCmhIVJmHCh0cAAACEmsbVAAAAARIAEAAn5oEJTKhNSLiMu%2FmDek3U" naturalheight="2448" naturalwidth="3264" src="https://attachment.outlook.office.net/owa/lkear@outlook.com/service.svc/s/GetFileAttachment?id=AQMkADAwATM0MDAAMS1hZGQAZS1hNjc1LTAwAi0wMAoARgAAA9Kr6IbgTyBAiSRBdO1YQT0HAKhZcJx8%2BFJCmhIVJmHCh0cAAAIBDAAAAKhZcJx8%2BFJCmhIVJmHCh0cAAACEmsbVAAAAARIAEAAn5oEJTKhNSLiMu%2FmDek3U&amp;X-OWA-CANARY=wrNoV-hpw0ikakuFs2j0EBBjwxxOLtQYBbYVAYl_nzTl03dyDHhfAsQn1bO4Xt9mlMBaDxQMWOg.&amp;token=b0d0b4ee-6d87-4c16-ad89-24b835ab2750&amp;owa=outlook.live.com&amp;isc=1" id="x_ymail_attachmentId107" class="x_inline-image-global x_inlined-image-cid-edbae9bb-12c7-4ab1-ba6f-1c9b5fadd597" style="width: 100%; max-width: 800px;">
		//outlook inline: <img originalsrc="cid:f1584c18-3cbc-f684-0a0c-72fac1e953ed@yahoo.com" data-custom="AQMkADAwATM0MDAAMS1hZGQAZS1hNjc1LTAwAi0wMAoARgAAA9Kr6IbgTyBAiSRBdO1YQT0HAKhZcJx8%2BFJCmhIVJmHCh0cAAAIBDAAAAKhZcJx8%2BFJCmhIVJmHCh0cAAACFKNtoAAAAARIAEAAkccfeSfCMT5TiKpfbl8Ae" naturalheight="800" naturalwidth="618" src="https://attachment.outlook.office.net/owa/lkear@outlook.com/service.svc/s/GetFileAttachment?id=AQMkADAwATM0MDAAMS1hZGQAZS1hNjc1LTAwAi0wMAoARgAAA9Kr6IbgTyBAiSRBdO1YQT0HAKhZcJx8%2BFJCmhIVJmHCh0cAAAIBDAAAAKhZcJx8%2BFJCmhIVJmHCh0cAAACFKNtoAAAAARIAEAAkccfeSfCMT5TiKpfbl8Ae&amp;X-OWA-CANARY=pvyBaouKP0-hV5px-DboHxBq6IJPLtQY8Jm4A7ofKdJ__HMJ4iV40iumnlZaKsDSYeIn-JYx0tA.&amp;token=51486cbe-95bc-4b63-bb64-f3c3a2b7e795&amp;owa=outlook.live.com&amp;isc=1" class="x_ymail-preserve-class x_inline-image-guid-2e06d212-412b-8f5c-73e0-ad130fe8af66 x_rte-inline-saved-image" alt="Inline image" id="x_yui_3_16_0_ym19_1_1482755864019_131019" style="width: 100%; max-width: 618px; max-height: 800px;">
		if (hasInline) {
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

	/*private void parseParts(int num, String subj, Part part) throws MessagingException, IOException {
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
	}*/
}
