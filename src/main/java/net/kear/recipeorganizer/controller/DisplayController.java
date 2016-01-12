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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.UserInfo;
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
	
	/****************************/
	/*** List recipes handler ***/
	/****************************/
	@RequestMapping("recipe/listRecipes")
	public String listRecipes(ModelMap model, Locale locale) {
		logger.info("recipe/listRecipes");
	
		String title = messages.getMessage("menu.submittedrecipes", null, "Submitted Recipes", locale);
		
		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.listRecipes(user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", false);
		model.addAttribute("recipes", recipes);

		return "recipe/listRecipes";
	}

	@RequestMapping("recipe/favorites")
	public String favoriteRecipeS(ModelMap model, Locale locale) {
		logger.info("recipe/favoriteRecipes");
		
		String title = messages.getMessage("menu.favorites", null, "Favorites", locale);

		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.favoriteRecipes(user.getId());
		model.addAttribute("userId", user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", true);
		model.addAttribute("recipes", recipes);

		return "recipe/listRecipes";
	}
	
	/***************************/
	/*** View recipe handler ***/
	/***************************/
	@RequestMapping("recipe/viewRecipe/{recipeId}")
	public String viewRecipe(ModelMap model, @RequestHeader("referer") String refer, @PathVariable Long recipeId, 
			HttpServletResponse response, HttpServletRequest request) {
		logger.info("recipe/viewRecipe GET");

		User user = (User)userInfo.getUserDetails();
		Recipe recipe = recipeService.getRecipe(recipeId);
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
		
		if (!refer.contains("edit")) {
			request.getSession().setAttribute("returnLabel", getReturnMessage(refer));
			request.getSession().setAttribute("returnUrl", refer);
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
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		
		recipeService.addView(recipe);

		return "recipe/viewRecipe";
	}
	
	@RequestMapping(value = "recipe/photo", method = RequestMethod.GET)
	public void getPhoto(@RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("photo GET");

		if (!fileName.isEmpty())
			fileAction.downloadFile(fileName, response);
	}
	
	@RequestMapping(value = "/report/getHtmlRpt/{id}", method = RequestMethod.GET)
	public void getHtmlRpt(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {
		logger.info("report/getHtmlRpt");

		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/recipe.jasper"));    	
    	
    	String dirPath = request.getSession().getServletContext().getRealPath("/jasper/");
    	File reportsDir = new File(dirPath);

		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(id);
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

    	} catch (JRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        response.setContentType("text/html");
        response.setHeader("Content-disposition", "inline; filename=TestPrint.html");
        ServletOutputStream outStream = null;
    	
        try {
        	outStream = response.getOutputStream();
        	baos.writeTo(outStream);

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/report/getPdfRpt/{id}", method = RequestMethod.GET)
	public void getPdfRpt(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {
		logger.info("report/getHtmlRpt");

		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/recipe.jasper"));    	
    	
    	String dirPath = request.getSession().getServletContext().getRealPath("/jasper/");
    	File reportsDir = new File(dirPath);

		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(id);
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
        	
        } catch (JRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        response.setContentType("text/html");
        response.setHeader("Content-disposition", "inline; filename=TestPrint.html");
        ServletOutputStream outStream = null;
    	
        try {
        	outStream = response.getOutputStream();
        	baos.writeTo(outStream);

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*************************/
	/*** Favorites handler ***/
	/*************************/
	@RequestMapping(value = "/recipe/addFavorite", method = RequestMethod.POST)
	@ResponseBody
	public String addFavorite(@RequestBody Favorites favorite, HttpServletResponse response) {
		logger.info("recipe/addFavorite");
		logger.info("userId=" + favorite.getId().getUserId());
		logger.info("recipeId=" + favorite.getId().getRecipeId());
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			recipeService.addFavorite(favorite);
		} catch (DataAccessException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = ExceptionUtils.getRootCauseMessage(ex);			
		}
		
		return msg;
	}
	
	@RequestMapping(value = "/recipe/removeFavorite", method = RequestMethod.POST)
	@ResponseBody
	public String removeFavorite(@RequestBody Favorites favorite, HttpServletResponse response) {
		logger.info("recipe/removeFavorite");
		logger.info("userId=" + favorite.getId().getUserId());
		logger.info("recipeId=" + favorite.getId().getRecipeId());
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			recipeService.removeFavorite(favorite);
		} catch (DataAccessException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = ExceptionUtils.getRootCauseMessage(ex);			
		}
		
		return msg;
	}

	/************************/
	/*** LastMade handler ***/
	/************************/
	@RequestMapping(value = "/recipe/recipeMade", method = RequestMethod.POST)
	@ResponseBody
	public String updateRecipeMade(@RequestBody RecipeMade recipeMade, HttpServletResponse response) {
		logger.info("recipe/updateRecipeMade");
		logger.info("userId=" + recipeMade.getId().getUserId());
		logger.info("recipeId=" + recipeMade.getId().getRecipeId());
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			recipeService.updateRecipeMade(recipeMade);
		} catch (DataAccessException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = ExceptionUtils.getRootCauseMessage(ex);			
		}
		
		return msg;
	}

	/**************************/
	/*** RecipeNote handler ***/
	/**************************/
	@RequestMapping(value = "/recipe/recipeNote", method = RequestMethod.POST)
	@ResponseBody
	public String updateRecipeNote(@RequestBody RecipeNote recipeNote, HttpServletResponse response) {
		logger.info("recipe/updateRecipeNote");
		logger.info("userId=" + recipeNote.getId().getUserId());
		logger.info("recipeId=" + recipeNote.getId().getRecipeId());
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			recipeService.updateRecipeNote(recipeNote);
		} catch (DataAccessException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = ExceptionUtils.getRootCauseMessage(ex);			
		}
		
		return msg;
	}

	/*****************************/
	/*** RecipeComment handler ***/
	/*****************************/
	@RequestMapping(value = "/recipe/recipeComment", method = RequestMethod.POST)
	@ResponseBody
	public String addRecipeComment(@RequestBody RecipeComment recipeComment, HttpServletResponse response) {
		logger.info("recipe/addRecipeComment");
		logger.info("userId=" + recipeComment.getUserId());
		logger.info("recipeId=" + recipeComment.getRecipeId());
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			commentService.addComment(recipeComment);
		} catch (DataAccessException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = ExceptionUtils.getRootCauseMessage(ex);			
		}
		
		return msg;
	}
	
	/****************************/
	/*** Navigation from view ***/
	/****************************/
	public String getReturnMessage(String referer) {

		String returnLabel = null;
		
		logger.info("referer: " + referer);
		if (referer.contains("searchResults"))
			returnLabel = "title.searchresults";
		else
		if (referer.contains("listRecipes"))
			returnLabel = "menu.submittedrecipes";
		else
		if (referer.contains("favorites"))
			returnLabel = "menu.favorites";
		else
		if (referer.contains("dashboard"))
			returnLabel = "dashboard.head";
		else
			returnLabel = "";

		return returnLabel;
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ModelAndView handleDataAccessException(DataAccessException ex) {
		ModelAndView view = new ModelAndView("/errors/errorData");

		logger.info("Recipe: DataAccessException exception!");
		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		view.addObject("errorMessage", errorMsg);
		return view;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex) {
		ModelAndView view = new ModelAndView("/errors/errorData");

		logger.info("Recipe: Exception exception!");
		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		view.addObject("errorMessage", errorMsg);
		return view;
	}
}
