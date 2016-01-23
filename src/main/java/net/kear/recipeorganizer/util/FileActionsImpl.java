package net.kear.recipeorganizer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;

public class FileActionsImpl implements FileActions {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired
    private Environment env;
	@Autowired
	private ExceptionLogService logService;

	private String avatarDir;	
	private String recipeDir;
	
	public FileActionsImpl() {}
	
	public void setAvatarDir(String dir) {
		this.avatarDir = dir;
	}

	public String getAvatarDir() {
		return this.avatarDir;
	}
	
	public void setRecipeDir(String dir) {
		recipeDir = dir;
	}

	public String getRecipeDir() {
		return this.recipeDir;
	}
	
	public boolean uploadFile(Recipe recipe, RequestContext requestContext) {
	    
		ServletExternalContext context = (ServletExternalContext) requestContext.getExternalContext();
	    MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest((HttpServletRequest)context.getNativeRequest());
	    MultipartFile file = multipartRequest.getFile("file");
	    
	    if (file == null)
	    	return true;
	    
		if (file != null && !file.isEmpty()) {
            try {
            	String filePath = recipeDir + file.getOriginalFilename();
            	logger.info("originalname = " + file.getOriginalFilename());
            	logger.info("name = " + file.getName());
            	logger.info("filePath = " + filePath);
            	File dest = new File(filePath);
            	file.transferTo(dest);
                recipe.setPhotoName(file.getOriginalFilename());
                logger.info("Successful upload");                
            } catch (IOException ex) {
            	logService.addException(ex);
            	return false;
            }
        }
		
		return true;
	}

	public boolean uploadFile(FileTypes fileType, long id, MultipartFile file) {
		
	    try {
	    	String filePath = (fileType == FileTypes.RECIPE ? recipeDir : avatarDir) + id + "." + file.getOriginalFilename();
	    	logger.info("originalname = " + file.getOriginalFilename());
	    	logger.info("filePath = " + filePath);
	        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	        stream.write(file.getBytes());
	        stream.close();
	        logger.info("Successful upload");
	    } catch (IOException ex) {
	    	logService.addException(ex);
	    	return false;
	    }
	    
	    return true;
	}	
	
	public boolean downloadFile(FileTypes fileType, long id, String fileName, HttpServletResponse response) {

        try {
        	String filePath = (fileType == FileTypes.RECIPE ? recipeDir : avatarDir) + id + "." + fileName;
        	File file = new File(filePath);
        	Path path = file.toPath();
        	ServletOutputStream stream = response.getOutputStream();
        	Files.copy(path, stream);
        	stream.flush();
        	logger.info("Successful download");                
        } catch (IOException ex) {
        	logService.addException(ex);
        	return false;
        }
        
        return true;
	}
	
	public boolean renameFile(FileTypes fileType, String oldName, String newName) {

		try {
			String file = (fileType == FileTypes.RECIPE ? recipeDir : avatarDir) + oldName;
			File srcFile = new File(file);
			Path srcPath = srcFile.toPath();
			Files.move(srcPath, srcPath.resolveSibling(newName));
        } catch (IOException ex) {
        	logService.addException(ex);
        	return false;
        }
		
		return true;
	}
	
	public boolean deleteFile(FileTypes fileType, long id, String fileName) {

		try {
			String file = (fileType == FileTypes.RECIPE ? recipeDir : avatarDir) + id + "." + fileName;
			File srcFile = new File(file);
			Path srcPath = srcFile.toPath();
			Files.delete(srcPath);
        } catch (IOException ex) {
        	logService.addException(ex);
        	return false;
        }
		
		return true;
	}

	public String fileExists(FileTypes fileType, long id) {

		String file = (fileType == FileTypes.RECIPE ? recipeDir : avatarDir) + id + ".*";
		File testfile = new File(file);
		FileFilter filter = new WildcardFileFilter(file);
		File files[] = testfile.listFiles(filter);
		return (files.length > 0 ? files[0].getName() : "");
	}
}
