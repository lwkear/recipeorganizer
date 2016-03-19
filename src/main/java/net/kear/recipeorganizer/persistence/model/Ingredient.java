package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "INGREDIENT")
public class Ingredient implements Serializable {

	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface NotBlankGroup {}
	public interface SizeGroup {}
	
	@GroupSequence({NotBlankGroup.class,SizeGroup.class})
	public interface IngredientGroup {}
	
	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INGREDIENT_SEQ")
	@SequenceGenerator(name = "INGREDIENT_SEQ", sequenceName = "INGREDIENT_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "NAME", nullable = false)
	@NotBlank(groups=NotBlankGroup.class)
	@Size(max=250, groups=SizeGroup.class)	//250
	private String name;
	
	@Column(name = "REVIEWED")
	private boolean reviewed;
	
	public Ingredient() {}
	
	public Ingredient(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Ingredient other = (Ingredient) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ingredient [id=" + id 
				+ ", name=" + name 
				+ ", reviewed=" + reviewed + "]"; 
	}
}