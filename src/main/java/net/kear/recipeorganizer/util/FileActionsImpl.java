package net.kear.recipeorganizer.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.util.FileResult;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	@Autowired
	private MessageSource messages;

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
	
	public FileResult uploadFile(Recipe recipe, RequestContext requestContext) {
	    
		ServletExternalContext context = (ServletExternalContext) requestContext.getExternalContext();
	    MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest((HttpServletRequest)context.getNativeRequest());
	    MultipartFile file = multipartRequest.getFile("file");
	    
	    if (file == null)
	    	return FileResult.NO_FILE;
	    
		if (file != null && !file.isEmpty()) {
            try {
            	/*String filePath = recipeDir + file.getOriginalFilename();
            	logger.info("originalname = " + file.getOriginalFilename());
            	logger.info("name = " + file.getName());
            	logger.info("filePath = " + filePath);
            	File dest = new File(filePath);
            	file.transferTo(dest);
                recipe.setPhotoName(file.getOriginalFilename());
                logger.info("Successful upload");*/                

    	    	String filePath = recipeDir + recipe.getId() + "." + file.getOriginalFilename();
    	    	logger.info("originalname = " + file.getOriginalFilename());
    	    	logger.info("filePath = " + filePath);
    	        	    	
    	    	//if the file is not an image, ImageIO returns null
    	    	BufferedImage img = ImageIO.read(file.getInputStream());
    	    	if (img == null)
    	    		return FileResult.NOT_IMAGE;
    	    	
    	    	String originalName = file.getOriginalFilename();
    	    	List<String> nameParts = Arrays.asList(originalName.split("[.]"));
    	    	int size = nameParts.size();
    	    	if (size <= 1)
    	    		return FileResult.NO_EXTENSION;
    	    	
    	    	String ext = nameParts.get(size-1);
    	    	logger.info("extension = " + ext);
    	    	ext = ext.toLowerCase();
    	    	if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("gif"))
    	    		return FileResult.INVALID_TYPE;

    	    	BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
    	    	ImageIO.write(img, ext, stream);
    	    	recipe.setPhotoName(file.getOriginalFilename());
    	        logger.info("Successful upload");
            
            } catch (IOException ex) {
            	logService.addException(ex);
            	return FileResult.EXCEPTION_ERROR;
            }
        }
		
		return FileResult.SUCCESS;
	}

	public FileResult uploadFile(FileType fileType, long id, MultipartFile file) {
		
	    /*try {
	    	String filePath = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + id + "." + file.getOriginalFilename();
	    	logger.info("originalname = " + file.getOriginalFilename());
	    	logger.info("filePath = " + filePath);
	        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	        stream.write(file.getBytes());
	        stream.close();
	        logger.info("Successful upload");
	    } catch (IOException ex) {
	    	logService.addException(ex);
	    	return false;
	    }*/

	    if (file == null)
	    	return FileResult.NO_FILE;
		
	    try {
	    	String filePath = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + id + "." + file.getOriginalFilename();
	    	logger.info("originalname = " + file.getOriginalFilename());
	    	logger.info("filePath = " + filePath);
	        	    	
	    	//if the file is not an image, ImageIO returns null
	    	BufferedImage img = ImageIO.read(file.getInputStream());
	    	if (img == null)
	    		return FileResult.NOT_IMAGE;
	    	
	    	String originalName = file.getOriginalFilename();
	    	List<String> nameParts = Arrays.asList(originalName.split("[.]"));
	    	int size = nameParts.size();
	    	if (size <= 1)
	    		return FileResult.NO_EXTENSION;
	    	
	    	String ext = nameParts.get(size-1);
	    	logger.info("extension = " + ext);
	    	ext = ext.toLowerCase();
	    	if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("gif"))
	    		return FileResult.INVALID_TYPE;

	    	BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	    	ImageIO.write(img, ext, stream);
	        logger.info("Successful upload");

	    } catch (IOException ex) {
	    	logService.addException(ex);
	    	return FileResult.EXCEPTION_ERROR;
	    }
	    
	    return FileResult.SUCCESS;
	}	
	
	public boolean downloadFile(FileType fileType, long id, String fileName, HttpServletResponse response) {

        try {
        	String filePath = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + id + "." + fileName;
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
	
	public boolean renameFile(FileType fileType, String oldName, String newName) {

		try {
			String file = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + oldName;
			File srcFile = new File(file);
			Path srcPath = srcFile.toPath();
			Files.move(srcPath, srcPath.resolveSibling(newName));
        } catch (IOException ex) {
        	logService.addException(ex);
        	return false;
        }
		
		return true;
	}
	
	public boolean deleteFile(FileType fileType, long id, String fileName) {

		try {
			String file = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + id + "." + fileName;
			File srcFile = new File(file);
			Path srcPath = srcFile.toPath();
			Files.delete(srcPath);
        } catch (IOException ex) {
        	logService.addException(ex);
        	return false;
        }
		
		return true;
	}

	public String fileExists(FileType fileType, long id) {

		String file = (fileType == FileType.RECIPE ? recipeDir : avatarDir) + id + ".*";
		File testfile = new File(file);
		FileFilter filter = new WildcardFileFilter(file);
		File files[] = testfile.listFiles(filter);
		return (files.length > 0 ? files[0].getName() : "");
	}
	
	public String getErrorMessage(FileResult fileResult, Locale locale) {
		String msg = "";
		String defaultMsg = "File upload error encountered.";
		
		switch (fileResult) {
			case SUCCESS			:	msg = messages.getMessage("exception.file.success", null, defaultMsg, locale);
										break;
			case NO_FILE			:	msg = messages.getMessage("exception.file.nofile", null, defaultMsg, locale);
										break;
			case NOT_IMAGE			:	msg = messages.getMessage("exception.file.notanimage", null, defaultMsg, locale);
										break;
			case NO_EXTENSION 		:	msg = messages.getMessage("exception.file.noextension", null, defaultMsg, locale);
										break;
			case INVALID_TYPE 		:	msg = messages.getMessage("exception.file.invalidtype", null, defaultMsg, locale);
										break;
			case EXCEPTION_ERROR	:	msg = messages.getMessage("exception.file.exceptionerror", null, defaultMsg, locale);
										break;
		}
		
		return msg;
	}
}
