package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.persistence.model.Source;

public interface SourceRepository {

	public void addSource(Source source);
	public List<String> getSources(String searchStr, SourceType type);    
}
