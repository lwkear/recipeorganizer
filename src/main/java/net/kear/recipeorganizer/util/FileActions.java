package net.kear.recipeorganizer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;

@Component
public class FileActions {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	public String uploadFile(Recipe recipe, RequestContext requestContext) {
	    ServletExternalContext context = (ServletExternalContext) requestContext.getExternalContext();
	    MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest((HttpServletRequest)context.getNativeRequest());
	    MultipartFile file = multipartRequest.getFile("file");
	    
	    String result = "";
	    
	    if (file == null)
	    	return result;
	    
		if (!file.isEmpty()) {
            try {
            	//TODO: CONFIG: get filepath from properties file
            	String filePath = "G:\\FileUploads\\" + file.getOriginalFilename();
            	logger.info("originalname = " + file.getOriginalFilename());
            	logger.info("name = " + file.getName());
            	logger.info("filePath = " + filePath);
            	File dest = new File(filePath);
            	file.transferTo(dest);
            	result = file.getName();
                recipe.setPhotoName(file.getOriginalFilename());
                logger.info("Successful upload");                
            } catch (Exception e) {
            	logger.info("Exception: " + e.getMessage());
            }
        } else {
        	logger.info("Empty file");
        }
	    
	    return result;
	}

	public String uploadFile(MultipartFile file) {
		
		String result = "";
		
	    try {
	    	String filePath = "G:\\FileUploads\\" + file.getOriginalFilename();
	    	logger.info("originalname = " + file.getOriginalFilename());
	    	logger.info("name = " + file.getName());
	    	logger.info("filePath = " + filePath);
	        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	        stream.write(file.getBytes());
	        stream.close();
	        logger.info("Successful upload");
	    } catch (Exception e) {
	    	logger.info("Exception: " + e.getMessage());
	    }
	    
	    return result;
	}	
	
	public String downloadFile(String fileName, HttpServletResponse response) {
		
		String result = "";
		
        try {
        	//TODO: CONFIG: get filepath from properties file
        	String filePath = "G:\\FileUploads\\" + fileName;
        	File file = new File(filePath);
        	Path path = file.toPath();
        	ServletOutputStream stream = response.getOutputStream();
        	Files.copy(path, stream);
        	stream.flush();
        	logger.info("Successful download");                
        } catch (Exception e) {
        	logger.info("Exception: " + e.getMessage());
        }

        return result;
	}
}
