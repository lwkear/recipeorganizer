package net.kear.recipeorganizer.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "MEASURE")
public class Measure {

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEASURE_SEQ")
	@SequenceGenerator(name = "MEASURE_SEQ", sequenceName = "MEASURE_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	public Measure() {}
	
	public Measure(String name) {
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

	@Override
	public String toString() {
		return "Category [id=" + id 
				+ ", name=" + name + "]";
	}	
}