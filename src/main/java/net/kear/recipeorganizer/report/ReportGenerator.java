package net.kear.recipeorganizer.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportGenerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String recipeReportFilePath = "";
	private String jasperReportDirPath = "";
	private File recipeFile = null;
	private File reportsDir = null;
	private JasperReport recipeReport = null;

	private ServletContext servletContext;
	
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private RecipeService recipeService;
	
	public ReportGenerator(ServletContext servletContext) {
		logger.debug("ReportGenerator");
		this.servletContext = servletContext;
	}

	public boolean configureReports() {
		this.recipeReportFilePath = this.servletContext.getRealPath("/jasper/recipe.jasper");
		this.jasperReportDirPath = this.servletContext.getRealPath("/jasper/");
		recipeFile = new File(this.recipeReportFilePath);
		reportsDir = new File(this.jasperReportDirPath);

    	try {
        	recipeReport = (JasperReport)JRLoader.loadObjectFromFile(recipeFile.getPath());
        	recipeReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        } catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
			return false;
		}

        return true;
	}

	public void createRecipeHtml(long recipeId, HttpServletResponse response) {
		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);
    	
    	try {
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	JasperPrint jasperPrint = JasperFillManager.fillReport(recipeReport, params, src);
        	
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
	
	public void createRecipePDF(long recipeId) {
		Map<String,Object> params = new HashMap<String,Object>();
		
		List<Recipe> list = new ArrayList<Recipe>();
		Recipe recipe = recipeService.getRecipe(recipeId);
		list.add(recipe);
    	JRDataSource src = new JRBeanCollectionDataSource(list);
    	
    	try {
        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	JasperPrint jasperPrint = JasperFillManager.fillReport(recipeReport, params, src);
        	
        	JRPdfExporter exporter = new JRPdfExporter();

        	String pdfFileName = "G:/recipeorganizer/recipe" + recipeId + ".pdf";
        	File pdfFile = new File(pdfFileName);
        	
        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfFile));
            exporter.exportReport();
        	
        } catch (JRException ex) {
			//log the error and do nothing - the client will notify the user of the lack of a report
			logService.addException(ex);
			return;
		}
	}
}
