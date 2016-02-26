package net.kear.recipeorganizer.util;

public class CategoryFacet {

	private long catId;
	private String catName;
	private long catCount;
	
	public CategoryFacet() {}
	
	public CategoryFacet(long catId, String catName, long catCount) {
		this.catId = catId;
		this.catName = catName;
		this.catCount = catCount;
	}

	public long getCatId() {
		return catId;
	}

	public void setCatId(long catId) {
		this.catId = catId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public long getCatCount() {
		return catCount;
	}

	public void setCatCount(long catCount) {
		this.catCount = catCount;
	}
}
