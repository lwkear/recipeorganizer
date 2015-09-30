package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Measure;
import net.kear.recipeorganizer.persistence.repository.MeasureRepository;
import net.kear.recipeorganizer.persistence.service.MeasureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class MeasureServiceImpl implements MeasureService {
 
    @Autowired
    private MeasureRepository measureRepository;
      
    public void addMeasure(Measure measure) {
    	measureRepository.addMeasure(measure);
    }
    
    public void updateMeasure(Measure measure) {
    	measureRepository.updateMeasure(measure);
    }
 
    public void deleteMeasure(Long id) {
    	measureRepository.deleteMeasure(id);
    }

    public List<Measure> listMeasure() {
    	return measureRepository.listMeasure();
    }
 
}