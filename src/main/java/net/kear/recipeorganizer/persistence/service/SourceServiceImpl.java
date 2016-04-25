package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.persistence.dto.SourceTypeDto;
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

    	return list;
    }
}