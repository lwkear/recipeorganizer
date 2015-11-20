package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import net.kear.recipeorganizer.persistence.model.Instruction;
//import net.kear.recipeorganizer.persistence.model.Recipe.MinSizeGroup2;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "INSTRUCTION_SECTION")
public class InstructionSection implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	public interface MinSizeGroup {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class,MinSizeGroup.class,Instruction.InstructGroup.class})
	public interface InstructSectGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTRUCTION_SECTION_SEQ")
	@SequenceGenerator(name = "INSTRUCTION_SECTION_SEQ", sequenceName = "INSTRUCTION_SECTION_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "SEQUENCE_NO", nullable = false)
	private int sequenceNo;
	
	@Column(name = "NAME")
	@NotBlank(groups=NotBlankGroup.class)
	@Size(max=10, groups=SizeGroup.class)	//50
	private String name;

	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="SECTION_ID", nullable=false)
	@Valid
	@Size(min=1, groups=MinSizeGroup.class)
	private List<Instruction> instructions = new AutoPopulatingList<Instruction>(Instruction.class);
	
	public InstructionSection() {}
	
	public InstructionSection(int seqNo, String name) {
		this.sequenceNo = seqNo;
		this.name = name;
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public Instruction getInstruction(int ndx) {
		return this.instructions.get(ndx);
	}

	public void setInstruction(List<Instruction> instructions) {
		this.instructions = instructions;
	}

	public void setInstruction(int ndx, Instruction instruction) {
		this.instructions.set(ndx, instruction);
	}
	
	public void addInstruction(Instruction instruction) {
		this.instructions.add(instruction);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		InstructionSection other = (InstructionSection) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (id != other.id)
			return false;
		if (sequenceNo != other.sequenceNo)
			return false;
		if (instructions == null) {
			if (other.instructions != null)
				return false;
		} else if (!instructions.equals(other.instructions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instruction [id=" + id  
				+ ", sequenceNo=" + sequenceNo 
				+ ", name=" + name + ", instructions=" + instructions + "]";
	}
}