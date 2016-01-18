package net.kear.recipeorganizer.persistence.repository;

import net.kear.recipeorganizer.persistence.model.ExceptionLog;
 
public interface ExceptionLogRepository {

    public void addException(ExceptionLog exceptionLog);
    public Long getEventId();
}
