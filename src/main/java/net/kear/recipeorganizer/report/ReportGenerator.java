package net.kear.recipeorganizer.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.FileHtmlResourceHandler;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.repo.FileRepositoryPersistenceServiceFactory;
import net.sf.jasperreports.repo.FileRepositoryService;
import net.sf.jasperreports.repo.PersistenceServiceFactory;
import net.sf.jasperreports.repo.RepositoryService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;

public class ReportGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String recipeHtmlReportFilePath = "";
	private String recipePdfReportFilePath = "";
	private String logoHtmlImagePath = "";
	private String logoPdfImagePath = "";
	private String jasperReportDirPath = "";
	private String pdfReportDirPath = "";
	private File recipeHtmlFile = null;
	private File recipePdfFile = null;
	private JasperReport recipeHtmlReport = null;
	private JasperReport recipePdfReport = null;	

	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private MessageSource messages;
	
	public ReportGenerator() {
		logger.debug("ReportGenerator");
	}

	public boolean configureReports(ServletContext servletContext, String pdfDir) {
		this.recipeHtmlReportFilePath = servletContext.getRealPath("/jasper/recipeHtml.jasper");
		this.recipePdfReportFilePath = servletContext.getRealPath("/jasper/recipePdf.jasper");
		this.logoHtmlImagePath = servletContext.getContextPath() + "/resources/images/logo.png";
		//this.logoHtmlImagePath = servletContext.getRealPath("/resources/images/logo.png");
		this.logoPdfImagePath = servletContext.getRealPath("/resources/images/logo.png");
		this.jasperReportDirPath = servletContext.getRealPath("/jasper/");
		this.pdfReportDirPath = pdfDir; 
		recipeHtmlFile = new File(this.recipeHtmlReportFilePath);
		recipePdfFile = new File(this.recipePdfReportFilePath);
		
		logger.debug("recipeHtmlReportFilePath: " + this.recipeHtmlReportFilePath);
		logger.debug("recipePdfReportFilePath: " + this.recipePdfReportFilePath);
		logger.debug("logoHtmlImagePath: " + this.logoHtmlImagePath);
		logger.debug("logoPdfImagePath: " + this.logoPdfImagePath);
		logger.debug("jasperReportDirPath: " + this.jasperReportDirPath);
		logger.debug("pdfReportDirPath: " + this.pdfReportDirPath);

    	try {
        	recipeHtmlReport = (JasperReport)JRLoader.loadObjectFromFile(recipeHtmlFile.getPath());
        	recipeHtmlReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	recipePdfReport = (JasperReport)JRLoader.loadObjectFromFile(recipePdfFile.getPath());
        	recipePdfReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        } catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
			return false;
		}

        return true;
	}

	public void createRecipeHtml(long userId, long recipeId, HttpServletResponse response, Locale locale) {
		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		recipe.setPrivateNotes(null);
		RecipeNote recipeNote = recipeService.getRecipeNote(userId, recipeId);
		if (recipeNote != null)
			recipe.setPrivateNotes(recipeNote.getNote());

		
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);

    	try {
        	//recipeHtmlReport = (JasperReport)JRLoader.loadObjectFromFile(recipeHtmlFile.getPath());
        	//recipeHtmlReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

    		SimpleJasperReportsContext context = new SimpleJasperReportsContext();
    	    FileRepositoryService fileRepository = new FileRepositoryService(context, jasperReportDirPath, false);
    	    context.setExtensions(RepositoryService.class, Collections.singletonList(fileRepository));
    	    context.setExtensions(PersistenceServiceFactory.class, Collections.singletonList(FileRepositoryPersistenceServiceFactory.getInstance()));
    	    MessageSourceResourceBundle bundle = new MessageSourceResourceBundle(messages, locale);
    	    
    	    //String logopath = this.getClass().getResource(logoHtmlImagePath).getPath();
    	    //logger.debug("logopath: " + logopath);
    	    
        	params.put("logoPath", logoHtmlImagePath);
        	params.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
        	JasperPrint jasperPrint = JasperFillManager.getInstance(context).fill(recipeHtmlReport, params, src);
    	    
        	HtmlExporter exporter = new HtmlExporter();

        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
            exporter.setConfiguration(reportExportConfiguration);
            
            SimpleHtmlExporterOutput htmlOutput = new SimpleHtmlExporterOutput(baos);
            //htmlOutput.setImageHandler(new FileHtmlResourceHandler(new File("html_images"), logoHtmlImagePath));
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
	
	public String createRecipePDF(long recipeId, Locale locale) throws JRException, JRRuntimeException {
		Map<String,Object> params = new HashMap<String,Object>();
		
		String pdfFileName = "";
		
		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);    	
    	
    	JRPdfExporter exporter = new JRPdfExporter();
    	pdfFileName = pdfReportDirPath + "recipe" + recipeId + ".pdf";
    	File pdfFile = new File(pdfFileName);
    	
    	if (pdfFile.exists()) {
    		long fileTime = pdfFile.lastModified();
    		if (fileTime != 0) {
    			Date fileDate = new Date(fileTime);
    			DateTime fileDateTime = new DateTime(fileDate);
    			Date recipeDate;
    			if (recipe.getDateUpdated() != null)
    				recipeDate = recipe.getDateUpdated();
    			else
    				recipeDate = recipe.getDateAdded();
    			DateTime recipeDateTime = new DateTime(recipeDate);
    			if (recipeDateTime.isBefore(fileDateTime))
    				return pdfFileName;
    		}
    	} 
    	
		SimpleJasperReportsContext context = new SimpleJasperReportsContext();
	    FileRepositoryService fileRepository = new FileRepositoryService(context, jasperReportDirPath, false);
	    context.setExtensions(RepositoryService.class, Collections.singletonList(fileRepository));
	    context.setExtensions(PersistenceServiceFactory.class, Collections.singletonList(FileRepositoryPersistenceServiceFactory.getInstance()));
	    MessageSourceResourceBundle bundle = new MessageSourceResourceBundle(messages, locale);
    	params.put("logoPath", logoPdfImagePath);
    	params.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
    	JasperPrint jasperPrint = JasperFillManager.getInstance(context).fill(recipePdfReport, params, src);
	    
    	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(expInput);
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfFile));
        exporter.exportReport();
    	
    	return pdfFileName;
	}
}