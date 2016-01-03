package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "INSTRUCTIONS")
public class Instruction implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class})
	public interface InstructGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTRUCTIONS_SEQ")
	@SequenceGenerator(name = "INSTRUCTIONS_SEQ", sequenceName = "INSTRUCTIONS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "SEQUENCE_NO", nullable = false)
	private int sequenceNo;
	
	@Column(name = "DESCRIPTION")
	@NotBlank(groups=NotBlankGroup.class)
	@Size(max=2000, groups=SizeGroup.class)	//2000
	private String description;

	//@ManyToOne //(fetch=FetchType.EAGER) //(cascade=CascadeType.ALL)
	//@JoinColumn(name="SECTION_ID") //, nullable = false, updatable=false)
	//private InstructionSection instructionSection;
	
	public Instruction() {}
	
	public Instruction(int seqNo, String desc) {
		this.sequenceNo = seqNo;
		this.description = desc;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	//public InstructionSection getInstructionSection() {
	//	return instructionSection;
	//}

	/*public void setInstructionSection(InstructionSection instructionSection) {
		this.instructionSection = instructionSection;
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + sequenceNo;
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
		Instruction other = (Instruction) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (sequenceNo != other.sequenceNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instruction [id=" + id  
				+ ", sequenceNo=" + sequenceNo 
				+ ", description=" + description + "]";
	}
}