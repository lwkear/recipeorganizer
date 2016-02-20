package net.kear.recipeorganizer.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.exception.RecipeNotFound;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.ViewReferer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Controller
public class DisplayController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private MessageSource messages;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private ViewReferer viewReferer;
	@Autowired
	private ExceptionLogService logService;
	
	/****************************/
	/*** List recipes handler ***/
	/****************************/
	@RequestMapping(value = "recipe/recipeList", method = RequestMethod.GET)
	public String listRecipes(ModelMap model, Locale locale) {
		logger.info("recipe/listRecipes GET");
	
		String title = messages.getMessage("menu.submittedrecipes", null, "Submitted Recipes", locale);
		
		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.listRecipes(user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", false);
		model.addAttribute("recipes", recipes);

		return "recipe/recipeList";
	}

	@RequestMapping(value = "recipe/favorites", method = RequestMethod.GET)
	public String favoriteRecipeS(ModelMap model, Locale locale) {
		logger.info("recipe/favoriteRecipes GET");
		
		String title = messages.getMessage("menu.favorites", null, "Favorites", locale);

		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.favoriteRecipes(user.getId());
		model.addAttribute("userId", user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", true);
		model.addAttribute("recipes", recipes);

		return "recipe/recipeList";
	}
	
	/***************************/
	/*** View recipe handler ***/
	/***************************/
	@RequestMapping(value = "recipe/viewRecipe/{recipeId}", method = RequestMethod.GET)
	public String viewRecipe(ModelMap model, @RequestHeader(value="referer", required=false) String refer, @PathVariable Long recipeId, 
			HttpServletResponse response, HttpServletRequest request, Locale locale) throws RecipeNotFound {
		logger.info("recipe/viewRecipe GET: recipeId=" + recipeId);

		User user = (User)userInfo.getUserDetails();
		Recipe recipe = null;
		
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			throw new RecipeNotFound(ex);
		}

		UserProfile profile = recipe.getUser().getUserProfile();
		
		String idStr = recipeId.toString();
		String cookieName = "recentRecipes";

		if (user != null) {
			Cookie recentRecipesCookie = cookieUtil.findUserCookie(request, cookieName, user.getId()); 
			if (recentRecipesCookie == null) {
				cookieUtil.setUserCookie(request, response, cookieName, user.getId(), idStr);
			}
			else {
				String recipeIds = recentRecipesCookie.getValue();
				ArrayList<String> cookieIds = new ArrayList<String>(Arrays.asList(recipeIds.split(",")));
				if (!cookieIds.contains(idStr)) {
					cookieIds.add(0, idStr);
					if (cookieIds.size() > 5)
						cookieIds.remove(5);				
				}
				else {
					cookieIds.remove(idStr);
					cookieIds.add(0, idStr);
				}
				String newStr = cookieIds.toString().replace("[", "").replace("]", "").replace(", ", ",");
				cookieUtil.setUserCookie(request, response, cookieName, user.getId(), newStr);
			}
		}
		
		if (refer != null && !refer.contains("edit")) {
			viewReferer.setReferer(refer, request);
		}
		
		boolean fav = recipeService.isFavorite(user.getId(), recipeId);
		RecipeMade recipeMade = recipeService.getRecipeMade(user.getId(), recipeId);
		RecipeNote recipeNote = recipeService.getRecipeNote(user.getId(), recipeId);
		long commentCount = commentService.getCommentCount(recipeId);
		List<CommentDto> commentList = commentService.listComments(recipeId);
				
		String jsonNote = null;
		if (recipeNote != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonNote = mapper.writeValueAsString(recipeNote);
			} catch (JsonProcessingException ex) {
				logService.addException(ex);
			}
		}
		
		model.addAttribute("madeCount", recipeMade.getMadeCount());
		model.addAttribute("lastMade", recipeMade.getLastMade());
		model.addAttribute("recipeNote", recipeNote.getNote());
		model.addAttribute("jsonNote", jsonNote);
		model.addAttribute("favorite", fav);
		model.addAttribute("commentCount", commentCount);
		model.addAttribute("commentList", commentList);
		model.addAttribute("recipe", recipe);
		model.addAttribute("submitJoin", recipe.getUser().getDateAdded());
		model.addAttribute("profile", profile);
		
		if (user.getId() != recipe.getUser().getId())
			recipeService.addView(recipe);
		if (refer != null && !refer.contains("edit"))
			viewReferer.setReferer(refer, request);

		return "recipe/viewRecipe";
	}
	
	@RequestMapping(value = "recipe/photo", method = RequestMethod.GET)
	public void getPhoto(@RequestParam("id") final long id, @RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("recipe/photo GET: fileName=" + fileName);

		if (fileName != null && !fileName.isEmpty())
			//errors are not fatal and will be logged by FileAction
			fileAction.downloadFile(FileType.RECIPE, id, fileName, response);
	}
	
	@RequestMapping(value = "/report/getHtmlRpt/{recipeId}", method = RequestMethod.GET)
	public void getHtmlRpt(HttpServletRequest request, HttpServletResponse response, @PathVariable Long recipeId) {
		logger.info("recipe/getHtmlRpt GET: recipeId=" + recipeId);

		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/recipe.jasper"));    	
    	
    	String dirPath = request.getSession().getServletContext().getRealPath("/jasper/");
    	File reportsDir = new File(dirPath);

		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);
    	
    	try {
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, src);
        	
        	HtmlExporter exporter = new HtmlExporter();

        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
            exporter.setConfiguration(reportExportConfiguration);
            
            SimpleHtmlExporterOutput htmlOutput = new SimpleHtmlExporterOutput(baos);
            exporter.setExporterOutput(htmlOutput);
            exporter.exportReport();

    	} catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
    		return;
		}
    	
        response.setContentType("text/html");
        response.setHeader("Content-disposition", "inline; filename=RecipePrint.html");
        ServletOutputStream outStream = null;
    	
        try {
        	outStream = response.getOutputStream();
        	baos.writeTo(outStream);
        } catch (IOException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
		}
	}

	@RequestMapping(value = "/report/getPdfRpt/{recipeId}", method = RequestMethod.GET)
	public void getPdfRpt(HttpServletRequest request, HttpServletResponse response, @PathVariable Long recipeId) {
		logger.info("recipe/getPdfRpt GET: recipeId=" + recipeId);

		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/recipe.jasper"));    	
    	
    	String dirPath = request.getSession().getServletContext().getRealPath("/jasper/");
    	File reportsDir = new File(dirPath);

		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);
    	
    	try {
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, src);
        	
        	JRPdfExporter exporter = new JRPdfExporter();

        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
            
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setPdfJavaScript("this.print({bUI: false,bSilent: true,bShrinkToFit: true});");
        	
        	exporter.setConfiguration(configuration);
        	exporter.exportReport();
        } catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
			return;
		}

        response.setContentType("text/html");
        response.setHeader("Content-disposition", "inline; filename=TestPrint.html");
        ServletOutputStream outStream = null;
    	
        try {
        	outStream = response.getOutputStream();
        	baos.writeTo(outStream);
        } catch (IOException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
		}
	}
	
	/*************************/
	/*** Favorites handler ***/
	/*************************/
	@RequestMapping(value = "/recipe/addFavorite", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject addFavorite(@RequestBody Favorites favorite) throws RestException {
		logger.info("recipe/addFavorite POST: user/recipe=" + favorite.getId().getUserId() + "/" + favorite.getId().getRecipeId());
		
		try {
			recipeService.addFavorite(favorite);
		} catch (Exception ex) {
			throw new RestException("exception.addFavorite", ex);
		}
		
		return new ResponseObject();
	}
	
	@RequestMapping(value = "/recipe/removeFavorite", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject removeFavorite(@RequestBody Favorites favorite) throws RestException {
		logger.info("recipe/removeFavorite POST: user/recipe=" + favorite.getId().getUserId() + "/" + favorite.getId().getRecipeId());
		
		try {
			recipeService.removeFavorite(favorite);
		} catch (DataAccessException ex) {
			throw new RestException("exception.removeFavorite", ex);
		}
		
		return new ResponseObject();
	}

	/************************/
	/*** LastMade handler ***/
	/************************/
	@RequestMapping(value = "/recipe/recipeMade", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject updateRecipeMade(@RequestBody RecipeMade recipeMade) throws RestException {
		logger.info("recipe/recipeMade POST: user/recipe=" + recipeMade.getId().getUserId() + "/" + recipeMade.getId().getRecipeId());
		
		try {
			recipeService.updateRecipeMade(recipeMade);
		} catch (DataAccessException ex) {
			throw new RestException("exception.recipeMade", ex);
		}
		
		return new ResponseObject();
	}

	/**************************/
	/*** RecipeNote handler ***/
	/**************************/
	@RequestMapping(value = "/recipe/recipeNote", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject updateRecipeNote(@RequestBody RecipeNote recipeNote) throws RestException {
		logger.info("recipe/recipeNote POST: user/recipe=" + recipeNote.getId().getUserId() + "/" + recipeNote.getId().getRecipeId());
		
		try {
			recipeService.updateRecipeNote(recipeNote);
		} catch (DataAccessException ex) {
			throw new RestException("exception.recipeNote", ex);
		}
		
		return new ResponseObject();
	}

	/*****************************/
	/*** RecipeComment handler ***/
	/*****************************/
	//NOTE: do NOT add @ResponseBody to this method since that will return a string instead of HTML
	@RequestMapping(value = "/recipe/recipeComment", method = RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.OK)
	public String addRecipeComment(Model model, @RequestBody RecipeComment recipeComment, HttpServletResponse response) throws RestException {
		logger.info("recipe/recipeComment POST: user/recipe=" + recipeComment.getUserId() + "/" + recipeComment.getRecipeId());
		
		try {
			commentService.addComment(recipeComment);
		} catch (Exception ex) {
			throw new RestException("exception.recipeComment", ex);
		}

		long userId = recipeComment.getUserId();
		long recipeId = recipeComment.getRecipeId();
		long commentCount = commentService.getCommentCount(recipeId);
		List<CommentDto> commentList = commentService.listComments(recipeId);
	
		Recipe recipe = new Recipe();
		recipe.setId(recipeId);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		model.addAttribute("commentCount", commentCount);
		model.addAttribute("commentList", commentList);
		model.addAttribute("viewerId", userId);
		model.addAttribute("recipe", recipe);
		
		return "recipe/comments";
	}

	@RequestMapping(value = "/recipe/flagComment/{recipeId}", method = RequestMethod.POST, produces="text/javascript")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject flagComment(@PathVariable Long recipeId) throws RestException {
		logger.info("recipe/flagComment POST: recipeId=" + recipeId);
		
		try {
			commentService.setCommentFlag(recipeId, 1);
		} catch (DataAccessException ex) {
			throw new RestException("exception.flagComment", ex);
		}
		
		return new ResponseObject();
	}
}
