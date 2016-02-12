package net.kear.recipeorganizer.util;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;

public interface FileActions {

	public FileResult uploadFile(Recipe recipe, RequestContext requestContext);
	public FileResult deleteFile(Recipe recipe);
	public FileResult renameFile(Recipe recipe);
	public FileResult uploadFile(FileType fileType, long id, MultipartFile file);
	public boolean downloadFile(FileType fileType, long id, String fileName, HttpServletResponse response);
	public boolean renameFile(FileType fileType, String oldName, String newName);
	public boolean deleteFile(FileType fileType, long id, String fileName);
	public String fileExists(FileType fileType, long id);
	public boolean fileExists(FileType fileType, long id, String fileName);
	public String getErrorMessage(FileResult fileResult, Locale locale);	
}
