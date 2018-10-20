package net.kear.recipeorganizer.util.maint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

//import org.apache.commons.configuration2.ex.ConfigurationException;
//import org.apache.commons.configuration2.PropertiesConfiguration;
//import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//G:\STSWorkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp2\wtpwebapps\recipeorganizer\WEB-INF\properties
//################### Maintenance Window Configuration ##########################
//maint.enabled=true
//maint.daysofweek=1,3,5
//maint.starttime=01:00
//maint.duration=60

//TODO: changed daysofweek to 1 - error parsing 1,3,5

public class MaintenanceProperties {

	@Autowired
	private ExceptionLogService logService;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String ENABLED_PROP = "maint.enabled";
	public static final String DAYS_OF_WEEK_PROP = "maint.daysofweek";
	public static final String START_TIME_PROP = "maint.starttime";
	public static final String DURATION_PROP = "maint.duration";
	public static final String PROP_FILE_NAME = "/WEB-INF/maintenance.properties";

	private PropertiesConfiguration propConfig;
	private ServletContext servletContext;
	
	public MaintenanceProperties(ServletContext servletContext) {
		logger.debug("MaintenanceProperties");
		this.servletContext = servletContext;
		if (servletContext == null)
			logger.debug("servletContext is null");
	}

	public boolean getPropertiesConfig() {
		logger.debug("setPropertieConfig");
		
		String filePath = "";
		if (servletContext != null) {
			filePath = servletContext.getRealPath(PROP_FILE_NAME);
			logger.debug("filePath: " + filePath);
		}
		else {
			logger.debug("servletContext is null");
			return false;
		}
		
		File propFile = new File(filePath);
		
		/*
		//TODO: this process changed in commons 2.3
		Configurations configs = new Configurations();
		
		try {
			propConfig = configs.properties(propFile);
		} catch (ConfigurationException ex) {
			logger.error(ex.getClass().toString(), ex);
			logService.addException(ex);
			return false;
		}
		*/
		
		try {
			propConfig = new PropertiesConfiguration(propFile);
		} catch (ConfigurationException ex) {
			logger.error(ex.getClass().toString(), ex);
			logService.addException(ex);
			return false;
		}
		
		//TODO: this process changed in commons 2.3
		FileChangedReloadingStrategy fileStrategy = new FileChangedReloadingStrategy();
		propConfig.setReloadingStrategy(fileStrategy);
		
		return true;
	}

	public boolean updateProperties(MaintenanceDto maintDto) {
		setBooleanProperty(ENABLED_PROP, maintDto.isMaintenanceEnabled());
		setIntegerArrayProperty(DAYS_OF_WEEK_PROP, maintDto.getDaysOfWeek());
		setStringProperty(START_TIME_PROP, maintDto.getStartTime());
		setIntegerProperty(DURATION_PROP, maintDto.getDuration());
		return saveConfig();
	}
	
	public String getStringProperty(String key) {		
		return propConfig.getString(key, "");
	}
	
	public boolean getBooleanProperty(String key) {
		return propConfig.getBoolean(key, false);
	}
	
	public Integer getIntegerProperty(String key) {
		return propConfig.getInteger(key, 0);
	}
	
	public List<Integer> getIntegerArrayProperty(String key) {
		String[] strs = propConfig.getStringArray(key);
		List<Integer> ints = new ArrayList<Integer>();
		for (String str : strs)
			ints.add(Integer.parseInt(str));	
		return ints;
	}

	public void setStringProperty(String key, String property) {
		propConfig.setProperty(key, property);
	}

	public void setBooleanProperty(String key, boolean property) {
		propConfig.setProperty(key, property);
	}
	
	public void setIntegerProperty(String key, Integer property) {
		propConfig.setProperty(key, property);
	}
	
	public void setIntegerArrayProperty(String key, List<Integer> property) {
		propConfig.setProperty(key, property);
	}
	
	public boolean saveConfig() {
		//TODO: this process changed in commons 2.3
		/*try {			
			propConfig.save();
		} catch (ConfigurationException ex) {
			logger.error(ex.getClass().toString(), ex);
			logService.addException(ex);
			return false;
		}*/
		
		return true;
	}
}
