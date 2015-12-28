package net.kear.recipeorganizer.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.UserInfo;
//import net.kear.recipeorganizer.persistence.service.UserService;
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
public class ViewRecipeController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private CookieUtil cookieUtil;
	
	@RequestMapping("recipe/listRecipes")
	public String listRecipeS(ModelMap model) {
		logger.info("recipe/listRecipes");
		
		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.listRecipes(user.getId());
		model.addAttribute("recipes", recipes);

		return "recipe/listRecipes";
	}

	@RequestMapping("recipe/viewRecipe/{id}")
	public String displayRecipe(ModelMap model, @PathVariable Long id, HttpServletResponse response, HttpServletRequest request) {
		logger.info("recipe/viewRecipe GET");

		User user = (User)userInfo.getUserDetails();
		Recipe recipe = recipeService.getRecipe(id);
		String idStr = id.toString();
		String cookieName = "recentRecipes";

		Cookie recentRecipesCookie = cookieUtil.findUserCookie(request, cookieName, user.getId()); 
		if (recentRecipesCookie == null) {
			cookieUtil.setUserCookie(request, response, cookieName, user.getId(), idStr);
		}
		else {
			String recipeIds = recentRecipesCookie.getValue();
			ArrayList<String> cookieIds = new ArrayList<String>(Arrays.asList(recipeIds.split(",")));
			if (!cookieIds.contains(idStr)) {
				cookieIds.add(0, idStr);
				if (cookieIds.size() > 3)
					cookieIds.remove(3);				
			}
			String newStr = cookieIds.toString().replace("[", "").replace("]", "").replace(", ", ",");
			cookieUtil.setUserCookie(request, response, cookieName, user.getId(), newStr);
		}
		
		model.addAttribute("recipe", recipe);

		return "recipe/viewRecipe";
	}
	
	@RequestMapping(value = "recipe/photo", method = RequestMethod.GET)
	public void getAvatar(@RequestParam("filename") final String fileName, HttpServletResponse response) {
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

/*
int size = recipe.getInstructSections().size();
if (size > 0) {
	Iterator<InstructionSection> iterator1 = recipe.getInstructSections().iterator();
	while (iterator1.hasNext()) {
		InstructionSection instructSection = iterator1.next();
		logger.info("id= " + instructSection.getId()); 
		logger.info("seq= " + instructSection.getSequenceNo());
		logger.info("name= " + instructSection.getName());
		size = instructSection.getInstructions().size();
		if (size > 0) {
			Iterator<Instruction> iterator2 = instructSection.getInstructions().iterator();
			while (iterator2.hasNext()) {
				Instruction instruct = iterator2.next();
				logger.info("id = " + instruct.getId()); 
				logger.info("desc= " + instruct.getDescription());
				logger.info("seq= " + instruct.getSequenceNo());			
			}					
		}
	}			
}

size = recipe.getIngredSections().size();
if (size > 0) {
	Iterator<IngredientSection> iterator1 = recipe.getIngredSections().iterator();
	while (iterator1.hasNext()) {
		IngredientSection ingredSection = iterator1.next();
		logger.info("id= " + ingredSection.getId()); 
		logger.info("seq= " + ingredSection.getSequenceNo());
		logger.info("name= " + ingredSection.getName());
		size = ingredSection.getRecipeIngredients().size();
		if (size > 0) {
			Iterator<RecipeIngredient> iterator2 = ingredSection.getRecipeIngredients().iterator();
			while (iterator2.hasNext()) {
				RecipeIngredient recipeIngred = iterator2.next();
				logger.info("id = " + recipeIngred.getId()); 
				logger.info("seq= " + recipeIngred.getSequenceNo());
				logger.info("name= " + recipeIngred.getIngredient().getName());
			}					
		}
	}			
}
*/