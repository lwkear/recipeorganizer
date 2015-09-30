package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.repository.SourceRepository;
import net.kear.recipeorganizer.persistence.service.SourceService;

@Service
@Transactional
public class SourceServiceImpl implements SourceService {

    @Autowired
    private SourceRepository sourceRepository;
	
    public void addSource(Source source) {
    	sourceRepository.addSource(source);
    }
    
    public List<String> getSources(String searchStr, String type)  {
    	return sourceRepository.getSources(searchStr, type);
    }
}