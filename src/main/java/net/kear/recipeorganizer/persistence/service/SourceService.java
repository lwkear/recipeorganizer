package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.persistence.dto.SourceTypeDto;
import net.kear.recipeorganizer.persistence.model.Source;

public interface SourceService {
     
	public void addSource(Source source);
	public List<String> getSources(String searchStr, SourceType type);
	public List<SourceTypeDto> getSourceTypes(Locale locale);
	public List<SourceTypeDto> getSourceTypes();
}