package net.kear.recipeorganizer.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.env.Environment;

public class ReportGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String recipeHtmlReportFilePath = "";
	private String recipePdfReportFilePath = "";
	private String logoHtmlImagePath = "";
	private String jasperReportDirPath = "";
	private String pdfReportDirPath = "";
	private File recipeHtmlFile = null;
	private File recipePdfFile = null;
	private File reportsDir = null;
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

	public boolean configureReports(ServletContext servletContext, Environment env) {
		this.recipeHtmlReportFilePath = servletContext.getRealPath("/jasper/recipeHtml.jasper");
		this.recipePdfReportFilePath = servletContext.getRealPath("/jasper/recipePdf.jasper");
		this.logoHtmlImagePath = servletContext.getContextPath() + "/resources/logo.png";
		this.jasperReportDirPath = servletContext.getRealPath("/jasper/");
		this.pdfReportDirPath = env.getProperty("file.directory.pdfs"); 
		recipeHtmlFile = new File(this.recipeHtmlReportFilePath);
		recipePdfFile = new File(this.recipePdfReportFilePath);
		reportsDir = new File(this.jasperReportDirPath);

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
    		//TODO: JASPER: REMOVE THESE TWO LINES when jasper report work is done
        	//recipeHtmlReport = (JasperReport)JRLoader.loadObjectFromFile(recipeHtmlFile.getPath());
        	//recipeHtmlReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        	MessageSourceResourceBundle bundle = new MessageSourceResourceBundle(messages, locale); 
        	params.put("logoPath", logoHtmlImagePath);
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	params.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
        	JasperPrint jasperPrint = JasperFillManager.fillReport(recipeHtmlReport, params, src);
        	
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
	
	public String createRecipePDF(long recipeId, Locale locale) {
		Map<String,Object> params = new HashMap<String,Object>();
		
		String pdfFileName = "";
		
		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);
    	
    	try {
    		//TODO: JASPER: REMOVE THESE TWO LINES when jasper report work is done
        	//recipePdfReport = (JasperReport)JRLoader.loadObjectFromFile(recipePdfFile.getPath());
        	//recipePdfReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        	MessageSourceResourceBundle bundle = new MessageSourceResourceBundle(messages, locale);
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	params.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
        	JasperPrint jasperPrint = JasperFillManager.fillReport(recipePdfReport, params, src);
        	
        	JRPdfExporter exporter = new JRPdfExporter();
        	pdfFileName = pdfReportDirPath + "recipe" + recipeId + ".pdf";
        	File pdfFile = new File(pdfFileName);
        	
        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfFile));
            exporter.exportReport();
        	
        } catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
			return null;
		}
    	
    	return pdfFileName;
	}
}