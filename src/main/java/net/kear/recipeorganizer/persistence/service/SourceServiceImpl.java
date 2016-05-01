package net.kear.recipeorganizer.persistence.service;
 
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.persistence.dto.SourceTypeDto;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.repository.SourceRepository;
import net.kear.recipeorganizer.persistence.service.SourceService;

//NOTE: webflow requires a service identifier
@Service("sourceService")
@Transactional
public class SourceServiceImpl implements SourceService {

	@Autowired
	private MessageSource messages;
    @Autowired
    private SourceRepository sourceRepository;
	
    public void addSource(Source source) {
    	sourceRepository.addSource(source);
    }
    
    public List<String> getSources(String searchStr, SourceType type)  {
    	return sourceRepository.getSources(searchStr, type);
    }

    public List<SourceTypeDto> getSourceTypes(Locale locale) {
    	List<SourceTypeDto> list = new AutoPopulatingList<SourceTypeDto>(SourceTypeDto.class);
    	for (SourceType type : SourceType.values()) {
    		SourceTypeDto dto = new SourceTypeDto();
    		dto.setType(type);
    		dto.setDisplayName(messages.getMessage("sourcetype." + type.name(), null, type.name(), locale));
    		list.add(dto);
    	}
    	
    	Collections.sort(list, new Comparator<SourceTypeDto>() {
			@Override
			public int compare(SourceTypeDto o1, SourceTypeDto o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
    	});
    	
    	return list;
    }

    public List<SourceTypeDto> getSourceTypes() {
    	Locale locale = LocaleContextHolder.getLocale();
    	List<SourceTypeDto> list = new AutoPopulatingList<SourceTypeDto>(SourceTypeDto.class);
    	for (SourceType type : SourceType.values()) {
    		SourceTypeDto dto = new SourceTypeDto();
    		dto.setType(type);
    		dto.setDisplayName(messages.getMessage("sourcetype." + type.name(), null, type.name(), locale));
    		list.add(dto);
    	}
    	
    	Collections.sort(list, new Comparator<SourceTypeDto>() {
			@Override
			public int compare(SourceTypeDto o1, SourceTypeDto o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
    	});
    	
    	return list;
    }
}