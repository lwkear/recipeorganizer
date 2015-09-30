package net.kear.recipeorganizer.util;

import java.sql.Date;

public class RecipeList {

	private long id;
	private long userId;
	private String name;	
	private boolean favorite;
	private boolean allowShare;
	private Date lastMade;
	private String firstName;
	private String lastName;
	private String category;
	private String source;

	public RecipeList() {}
	
	public RecipeList(long id, long userId, String name, boolean favorite, boolean allowShare, Date lastMade, String firstName, String lastName, String category, String source) {
		super();
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.favorite = favorite;
		this.allowShare = allowShare;
		this.lastMade = lastMade;
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
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public boolean isAllowShare() {
		return allowShare;
	}
	
	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
	}
	
	public Date getLastMade() {
		return lastMade;
	}
	
	public void setLastMade(Date lastMade) {
		this.lastMade = lastMade;
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
