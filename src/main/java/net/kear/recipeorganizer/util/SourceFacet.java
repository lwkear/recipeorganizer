package net.kear.recipeorganizer.util;

public class SourceFacet {

	private String sourceType;
	private String sourceName;
	private long sourceCount;
	
	public SourceFacet() {}

	public SourceFacet(String sourceType, String sourceName, long sourceCount) {
		this.sourceType = sourceType;
		this.sourceName = sourceName;
		this.sourceCount = sourceCount;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public long getSourceCount() {
		return sourceCount;
	}

	public void setSourceCount(long sourceCount) {
		this.sourceCount = sourceCount;
	}	
}
