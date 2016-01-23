package net.kear.recipeorganizer.util;

import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;

public interface FileActions {

	public boolean uploadFile(Recipe recipe, RequestContext requestContext);
	public boolean uploadFile(FileTypes fileType, long id, MultipartFile file);
	public boolean downloadFile(FileTypes fileType, long id, String fileName, HttpServletResponse response);
	public boolean renameFile(FileTypes fileType, String oldName, String newName);
	public boolean deleteFile(FileTypes fileType, long id, String fileName);
	public String fileExists(FileTypes fileType, long id);
	
}
