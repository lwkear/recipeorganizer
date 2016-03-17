package net.kear.recipeorganizer.util;

import java.io.Serializable;

public class DayOfWeek implements Serializable {
	
	private static final long serialVersionUID = 1L;	

	private Integer id;
	private String name;
	
	public DayOfWeek() {}

	public DayOfWeek(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		DayOfWeek other = (DayOfWeek) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DayOfWeek [id=" + id + ", name=" + name + "]";
	}	
}
