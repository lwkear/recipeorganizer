package net.kear.recipeorganizer.persistence.dto;

public class SearchResultsDto {
	//private String id;
	private long id;
	private String name;
	private String description;
	private String photo;
	
	public SearchResultsDto() {};
	
	/*public SearchResultsDto(String id, String name, String description, String photo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.photo = photo;
	};*/
	
	public SearchResultsDto(Long id, String name, String description, String photo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.photo = photo;
	};	

	/*public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}*/
	
	public long getId() {
		return id;
	}
	
	public void setId(Long id) {
		//this.id = String.valueOf(id);
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}	
}
