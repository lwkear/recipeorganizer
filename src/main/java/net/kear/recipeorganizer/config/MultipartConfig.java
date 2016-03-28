package net.kear.recipeorganizer.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MultipartConfig {

	@Autowired
    private Environment env;	
	
	public MultipartConfig() {}

	public MultipartConfigElement getMultipartConfig() {
		String tempDir = null;
		long maxFileSize = 1024*1024*5;
		long maxRequestSize = 1024*1024*5*5;
		int fileSizeThreshold = 1024*1024;
		MultipartConfigElement config = new MultipartConfigElement(tempDir, maxFileSize, maxRequestSize, fileSizeThreshold);
		return config;
	}
}