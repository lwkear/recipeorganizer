package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Measure;
 
public interface MeasureService {
     
    public void addMeasure(Measure measure);
    public void updateMeasure(Measure measure);
    public void deleteMeasure(Long id);
    public List<Measure> listMeasure();
}