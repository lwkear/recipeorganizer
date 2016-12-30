package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;

public class EmailDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private int msgNum;
	private String mimeId;
	private Flags flags;
	private boolean seen;
	private boolean deleted;
	private boolean recent;
	private boolean answered;
	private boolean hasAttachment;
	private String from;
	private String fromFull;
	private String to;
	private String toFull;
	private String cc;
	private String bcc;
	private String subject;
	private Date sentDate;
	private long sortDate;
	private String content;
	private String fileNames; 
	
	public EmailDto() {}

	public int getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(int msgNum) {
		this.msgNum = msgNum;
	}

	public String getMimeId() {
		return mimeId;
	}

	public void setMimeId(String mimeId) {
		this.mimeId = mimeId;
	}

	public Flags getFlags() {
		return flags;
	}

	public void setFlags(Flags flags) {
		this.flags = flags;
		this.seen = flags.contains(Flag.SEEN);
		this.deleted = flags.contains(Flag.DELETED);
		this.recent = flags.contains(Flag.RECENT);
		this.answered = flags.contains(Flag.ANSWERED);
	}

	public boolean isSeen() {
		return seen;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isRecent() {
		return recent;
	}

	public boolean isAnswered() {
		return answered;
	}
	
	public boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(Address[] from) {
		this.from = parseAddress(from, false);
		this.fromFull = parseAddress(from, true);
	}

	public String getFromFull() {
		return fromFull;
	}

	public void setFromFull(String fromFull) {
		this.fromFull = fromFull;
	}

	public String getTo() {
		return to;
	}

	public void setTo(Address[] to) {
		this.to = parseAddress(to, false);
		this.toFull = parseAddress(to, true);
	}

	public String getToFull() {
		return toFull;
	}

	public void setToFull(String toFull) {
		this.toFull = toFull;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(Address[] cc) {
		this.cc = parseAddress(cc, false);
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(Address[] bcc) {
		this.bcc = parseAddress(bcc, false);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
		setSortDate(sentDate.getTime());
	}

	public long getSortDate() {
		return sortDate;
	}

	public void setSortDate(long sortDate) {
		this.sortDate = sortDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	private String parseAddress(Address[] addressArray, boolean full) {
		String addresses = "";
		if ((addressArray == null) || (addressArray.length < 1))
			return null;
		if (!(addressArray[0] instanceof InternetAddress))
			return null;
		String separator = "";
		for (Address address : addressArray) {
			InternetAddress iAddress = (InternetAddress) address;
			String name = iAddress.getPersonal();
			name = StringUtils.remove(name, '\'');
			String addr = iAddress.getAddress();
			addr = StringUtils.remove(addr, '\'');
			//default to the addr
			String email = addr;
			if (!StringUtils.isEmpty(name))
				//preferably display the name
				email = name;
			if (full && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(addr) && !StringUtils.equals(name, addr))
				//add both name and addr if present
				email = name + " &lt;" + addr + "&gt;";
			addresses += separator + email;
			separator =  "; ";
		}
		return addresses;
	}
}
