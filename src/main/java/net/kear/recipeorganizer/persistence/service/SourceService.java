package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.model.Source;

public interface SourceService {
     
	public void addSource(Source source);
	public List<String> getSources(String searchStr, String type);
	public String getSourceDisplayName(String type, Locale locale);
}