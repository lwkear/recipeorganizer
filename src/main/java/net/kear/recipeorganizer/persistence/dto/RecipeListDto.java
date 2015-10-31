package net.kear.recipeorganizer.persistence.dto;

public class RecipeListDto {

	private long id;
	private long userId;
	private String name;	
	private boolean allowShare;
	private String firstName;
	private String lastName;
	private String category;
	private String source;

	public RecipeListDto() {}
	
	public RecipeListDto(long id, long userId, String name, boolean allowShare, String firstName, String lastName, String category, String source) {
		super();
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.allowShare = allowShare;
		this.firstName = firstName;
		this.lastName = lastName;
		this.category = category;
		this.source = source;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isAllowShare() {
		return allowShare;
	}
	
	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
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
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
}
