package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Source;

public interface SourceService {
     
	public void addSource(Source source);
	public List<String> getSources(String searchStr, String type);
}