package net.kear.recipeorganizer.persistence.dto;

import java.util.Date;

public class RecipeListDto {

	private long id;
	private String name;
	private String desc;
	private Date submitted;
	private String firstName;
	private String lastName;
	private String category;
	private String sourcetype;

	public RecipeListDto() {}
	
	public RecipeListDto(long id, String name, String desc, Date submitted, String firstName, String lastName, String category, String sourcetype) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.submitted = submitted;
		this.firstName = firstName;
		this.lastName = lastName;
		this.category = category;
		this.sourcetype = sourcetype;
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
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(String sourcetype) {
		this.sourcetype = sourcetype;
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}	

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
}
