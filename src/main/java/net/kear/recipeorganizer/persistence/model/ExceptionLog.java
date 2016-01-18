package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;	
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "EXCEPTION_LOG")
public class ExceptionLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXCEPTION_LOG_SEQ")
	@SequenceGenerator(name = "EXCEPTION_LOG_SEQ", sequenceName = "EXCEPTION_LOG_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "EVENT_ID", nullable = false)
	private long eventId;

	@Temporal(TemporalType.DATE)
	//@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "EVENT_TIMESTAMP")
	private Date eventTimestamp;

	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "MESSAGE")
	private String messsage;

	@Column(name = "CLASSNAME")
	private String className;

	@Column(name = "METHOD")
	private String method;

	@Column(name = "LINENUM")
	private String lineNum;
	
	public ExceptionLog() {}
	
	public ExceptionLog(long eventId, Date eventTimestamp, String userName, String messsage, String className, String method, String lineNum) {
		super();
		this.eventId = eventId;
		this.eventTimestamp = eventTimestamp;
		this.userName = userName;
		this.messsage = messsage;
		this.className = className;
		this.method = method;
		this.lineNum = lineNum;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public Date getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMesssage() {
		return messsage;
	}

	public void setMesssage(String messsage) {
		this.messsage = messsage;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLineNum() {
		return lineNum;
	}

	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result + ((eventTimestamp == null) ? 0 : eventTimestamp.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lineNum == null) ? 0 : lineNum.hashCode());
		result = prime * result + ((messsage == null) ? 0 : messsage.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		ExceptionLog other = (ExceptionLog) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (eventId != other.eventId)
			return false;
		if (eventTimestamp == null) {
			if (other.eventTimestamp != null)
				return false;
		} else if (!eventTimestamp.equals(other.eventTimestamp))
			return false;
		if (id != other.id)
			return false;
		if (lineNum == null) {
			if (other.lineNum != null)
				return false;
		} else if (!lineNum.equals(other.lineNum))
			return false;
		if (messsage == null) {
			if (other.messsage != null)
				return false;
		} else if (!messsage.equals(other.messsage))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (userName != other.userName)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExceptionLog [id=" + id + ", eventId=" + eventId + ", eventTimestamp=" + eventTimestamp + ", userName=" + userName + ", messsage=" + messsage + ", className="
				+ className + ", method=" + method + ", lineNum=" + lineNum + "]";
	}
}