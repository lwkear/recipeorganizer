package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.repository.SourceRepository;
import net.kear.recipeorganizer.persistence.service.SourceService;

@Service
@Transactional
public class SourceServiceImpl implements SourceService {

	@Autowired
	private MessageSource messages;
    @Autowired
    private SourceRepository sourceRepository;
	
    public void addSource(Source source) {
    	sourceRepository.addSource(source);
    }
    
    public List<String> getSources(String searchStr, String type)  {
    	return sourceRepository.getSources(searchStr, type);
    }
    
    public String getSourceDisplayName(String type, Locale locale) {

    	String field = "";
    	if (type.equalsIgnoreCase(Source.TYPE_COOKBOOK))
    		field = "cookbook";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_MAGAZINE))
    		field = "magazine";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_NEWSPAPER))
    		field = "newspaper";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_PERSON))
    		field = "person";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_WEBSITE))
    		field = "website";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_OTHER))
    		field = "other";
    	else
    	if (type.equalsIgnoreCase(Source.TYPE_NONE))
    		field = "none";
    	else
    		return null;

    	String lookup = "recipe.optional.source.select." + field; 
    	String sourceName = messages.getMessage(lookup, null, "None", locale);
    	return sourceName;
    }
}