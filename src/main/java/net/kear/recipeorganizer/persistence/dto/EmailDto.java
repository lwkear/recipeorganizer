package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;

import net.kear.recipeorganizer.util.email.Recipient;

import org.apache.commons.lang.StringUtils;


import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EmailDto implements Serializable {

	private static final long serialVersionUID = 1L;	

	private int msgNum;
	private long UID;
	@JsonIgnore
	private Flags flags;
	private boolean seen;
	private boolean deleted;
	private boolean recent;
	private boolean answered;
	private boolean hasAttachment;
	private List<Recipient> from;
	private List<Recipient> to;
	private List<Recipient> cc;
	private Object jsonFrom;
	private Object jsonTo;
	private Object jsonCc;
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

	public long getUID() {
		return UID;
	}

	public void setUID(long uID) {
		UID = uID;
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

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isRecent() {
		return recent;
	}

	public void setRecent(boolean recent) {
		this.recent = recent;
	}

	public boolean isAnswered() {
		return answered;
	}
	
	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public List<Recipient> getFrom() {
		return from;
	}

	public void setFrom(List<Recipient> from) {
		this.from = from;
	}

	public List<Recipient> getTo() {
		return to;
	}

	public void setTo(List<Recipient> to) {
		this.to = to;
	}

	public List<Recipient> getCc() {
		return cc;
	}

	public void setCc(List<Recipient> cc) {
		this.cc = cc;
	}

	public Object getJsonFrom() {
		return jsonFrom;
	}

	public void setJsonFrom(Object jsonFrom) {
		this.jsonFrom = jsonFrom;
	}

	public Object getJsonTo() {
		return jsonTo;
	}

	public void setJsonTo(Object jsonTo) {
		this.jsonTo = jsonTo;
	}

	public Object getJsonCc() {
		return jsonCc;
	}

	public void setJsonCc(Object jsonCc) {
		this.jsonCc = jsonCc;
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

	
	/*private String parseAddress(Address[] addressArray, boolean full) {
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
	}*/

	/*public String getFromNameStr() {
		return fromNameStr;
	}

	public void setFromNameStr(String fromNameStr) {
		this.fromNameStr = fromNameStr;
	}*/

	/*String json = "";
	ObjectMapper mapper = new ObjectMapper();
	try {
		json = mapper.writeValueAsString(this.fromName);
	} catch (JsonProcessingException e) {}
	this.fromNameStr = json;*/		
	
	/*public void setFrom(Address[] addressArray) {
		this.fromName = parseAddress(addressArray, AddressType.NAME);
		this.fromEmail = parseAddress(addressArray, AddressType.EMAIL);
		List<String> list = parseAddress(addressArray, AddressType.FULL);
		String full = "";
		String separator = "";
		if (list != null && !list.isEmpty()) {
			for (String addr : list) {
				full += separator + addr;
				separator = "?";
			}
		}
		this.fromFull = full;
	}

	public void setTo(Address[] addressArray) {
		this.toName = parseAddress(addressArray, AddressType.NAME);
		this.toEmail = parseAddress(addressArray, AddressType.EMAIL);
		List<String> list = parseAddress(addressArray, AddressType.FULL);
		String full = "";
		String separator = "";
		if (list != null && !list.isEmpty()) {
			for (String addr : list) {
				full += separator + addr;
				separator = "?";
			}
		}
		this.toFull = full;
	}

	public void setCc(Address[] addressArray) {
		this.ccName = parseAddress(addressArray, AddressType.NAME);
		this.ccEmail = parseAddress(addressArray, AddressType.EMAIL);
		List<String> list = parseAddress(addressArray, AddressType.FULL);
		String full = "";
		String separator = "";
		if (list != null && !list.isEmpty()) {
			for (String addr : list) {
				full += separator + addr;
				separator = "?";
			}
		}
		this.ccFull = full;
	}*/
	
	/*private List<String> parseAddress(Address[] addressArray, AddressType type) {
		if ((addressArray == null) || (addressArray.length < 1))
			return null;
		if (!(addressArray[0] instanceof InternetAddress))
			return null;
		List<String> addresses = new ArrayList<String>();
		for (Address address : addressArray) {
			InternetAddress iAddress = (InternetAddress) address;
			String personal = iAddress.getPersonal();
			if (!StringUtils.isEmpty(personal))
				personal = personal.replaceAll("'", "");
			String addr = iAddress.getAddress();
			if (type == AddressType.NAME) {
				String name = personal;
				if (StringUtils.isEmpty(name))
					//preferably display the name
					name = addr;
				addresses.add(name);				
			}
			else
			if (type == AddressType.EMAIL)
				addresses.add(addr);
			else
			if (type == AddressType.FULL) {
				String full = "";
				if (StringUtils.isEmpty(personal) || personal.equalsIgnoreCase(addr))
					full = addr;
				else
					full = personal + " &lt;" + addr + "&gt;";						
				addresses.add(full);
			}
		}
		return addresses;
	}*/
	
	public void setFrom(Address[] addressArray) {
		List<Recipient> recipients = new ArrayList<Recipient>();
		this.from = recipients;
		this.jsonFrom = "";
		if (!isValidAddressArray(addressArray))
			return;

		for (Address address : addressArray) {
			InternetAddress iAddr = (InternetAddress) address;
			recipients.add(getRecipient(iAddr));
		}
		
		this.from = recipients;
		this.jsonFrom = convertToJson(recipients);
	}

	public void setTo(Address[] addressArray) {
		List<Recipient> recipients = new ArrayList<Recipient>();
		this.to = recipients;
		this.jsonTo = "";
		if (!isValidAddressArray(addressArray))
			return;

		for (Address address : addressArray) {
			InternetAddress iAddr = (InternetAddress) address;
			recipients.add(getRecipient(iAddr));
		}
		
		this.to = recipients;
		this.jsonTo = convertToJson(recipients);
	}

	public void setCc(Address[] addressArray) {
		List<Recipient> recipients = new ArrayList<Recipient>();
		this.cc = recipients;
		this.jsonCc = "";
		if (!isValidAddressArray(addressArray))
			return;

		for (Address address : addressArray) {
			InternetAddress iAddr = (InternetAddress) address;
			recipients.add(getRecipient(iAddr));
		}
		
		this.cc = recipients;
		this.jsonCc = convertToJson(recipients);
	}
	
	private Recipient getRecipient(InternetAddress address) {
		Recipient recip = new Recipient();
		String personal = address.getPersonal();
		if (!StringUtils.isEmpty(personal))
			personal = personal.replaceAll("'", "");
		String addr = address.getAddress();
		String name = personal;
		if (StringUtils.isEmpty(name)) {
			//display the addr if no name
			name = addr;
			recip.setHasName(false);
		}
		recip.setName(name);
		recip.setEmail(addr);
		String full = "";
		if (StringUtils.isEmpty(personal) || personal.equalsIgnoreCase(addr))
			full = addr;
		else
			full = personal + " &lt;" + addr + "&gt;";
		recip.setFull(full);
		return recip;
	}

	private boolean isValidAddressArray(Address[] addressArray) {
		if ((addressArray == null) || (addressArray.length < 1))
			return false;
		if (!(addressArray[0] instanceof InternetAddress))
			return false;
		return true;
	}
	
	private String convertToJson(List<Recipient> recipients) {
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(recipients);
		} catch (JsonProcessingException e) {}
		return json;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (UID ^ (UID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailDto other = (EmailDto) obj;
		if (UID != other.UID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmailDto [msgNum=" + msgNum 
				+ ", UID=" + UID 
				+ ", flags=" + flags 
				+ ", seen=" + seen 
				+ ", deleted=" + deleted 
				+ ", recent=" + recent 
				+ ", answered=" + answered 
				+ ", hasAttachment=" + hasAttachment 
				+ ", from=" + from 
				+ ", to=" + to 
				+ ", cc=" + cc 
				+ ", jsonFrom=" + jsonFrom 
				+ ", jsonTo=" + jsonTo 
				+ ", jsonCc=" + jsonCc 
				+ ", subject=" + subject 
				+ ", sentDate="
				+ sentDate + ", sortDate=" 
				+ sortDate + ", content=" 
				+ content + ", fileNames=" 
				+ fileNames + "]";
	}
}
