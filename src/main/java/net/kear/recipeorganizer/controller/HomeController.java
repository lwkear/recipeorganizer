package net.kear.recipeorganizer.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private SessionRegistry sessionRegistry;

	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("getHome");

		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sessID = session.getId();

		logger.info("Session created on: " + createTime);
		logger.info("Session last accessed on: " + lastAccess);
		logger.info("Session expires after: " + maxInactive + " seconds");
		logger.info("Session ID: " + sessID);

		List<Object> allPrinc = sessionRegistry.getAllPrincipals();

		for (Object obj : allPrinc) {
			final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

			for (SessionInformation sess : sessions) {
				Object princ = sess.getPrincipal();
				String sessId = sess.getSessionId();
				Date sessDate = sess.getLastRequest();
				
				logger.info("sessionRegistry.princ: " + princ);
				logger.info("sessionRegistry.sessId: " + sessId);
				logger.info("sessionRegistry.sessDate: " + sessDate.toString());
			}
		}
		
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("getAbout");
		
		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model, HttpSession session) {
		logger.info("getThankyou");
	
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		return "thankyou";
	}
	
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	public String getFaq(Model model) {
		logger.info("getFaq");
		
		return "faq";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) {
		logger.info("getContact");
		
		return "contact";
	}
}
	
	
	/*@RequestMapping(value = "/printtest", method = RequestMethod.GET)
	public String getPrintTest(Model model) {
		logger.info("printtest");
		
		return "printtest";
	}*/
	
	/*@RequestMapping(value = "/report/getpdf", method = RequestMethod.GET)
	public void getReportPdf(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("report/getpdf");
		
		Map<String,Object> params = new HashMap<String,Object>();
		params = null;
    	//byte[] bytes = null;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	//OutputStreamExporterOutput expOutput = null;
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/TestPrint.jasper"));

        try {
        	 version #1 - works, but displays pdf
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
			bytes = JasperRunManager.runReportToPdf(jasperReport, params);
        	
        	version #2 - works, but displays pdf
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params);
        	JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        	
        	version #3 - works, pdf is displayed and print dialog appears automatically
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params);
        	
        	JRPdfExporter exporter = new JRPdfExporter();

        	SimpleExporterInput expInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(expInput);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
            
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setPdfJavaScript("this.print({bUI: false,bSilent: true,bShrinkToFit: true});");
        	
        	exporter.setConfiguration(configuration);
        	exporter.exportReport();
        	end of version #3
        	
		} catch (JRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        response.setContentType("application/x-pdf");
        response.setHeader("Content-disposition", "attachment; filename=TestPrint.pdf");
        response.setHeader("Content-disposition", "inline; filename=TestPrint.pdf");
        ServletOutputStream outStream = null;

        try {
			 version #1
        	outStream = response.getOutputStream();
	        outStream.write(bytes, 0, bytes.length);
	        outStream.flush();
	        outStream.close();
			
			version #2
			outStream = response.getOutputStream();
			response.setContentLength(baos.size());
	        baos.writeTo(outStream);
        	
        	version #3 
        	outStream = response.getOutputStream();
        	baos.writeTo(outStream);

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

/*	@RequestMapping(value = "/report/gethtmlConn", method = RequestMethod.GET)
	public void getReportHtmlConn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("report/gethtml");

		Map<String,Object> params = new HashMap<String,Object>();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
    	File reportFile = new File(request.getSession().getServletContext().getRealPath("/jasper/recipeConn.jasper"));    	
    	//File subreport1File = new File(request.getSession().getServletContext().getRealPath("/jasper/ingredients.jasper"));
    	//File subreport2File = new File(request.getSession().getServletContext().getRealPath("/jasper/instructions.jasper"));
    	
    	String dirPath = request.getSession().getServletContext().getRealPath("/jasper/");
    	File reportsDir = new File(dirPath);

    	Connection connection = null;
    	
    	try {
			connection = dataSource.getConnection();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	try {
        	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());
        	jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	
        	//the sub-reports didn't work this way!!!
        	//JasperReport jasperSubReport1 = (JasperReport)JRLoader.loadObjectFromFile(subreport1File.getPath());
        	//jasperSubReport1.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	//JasperReport jasperSubReport2 = (JasperReport)JRLoader.loadObjectFromFile(subreport2File.getPath());
        	//jasperSubReport2.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
        	//params.put("INGREDIENTRPT", jasperSubReport1);
        	//params.put("INSTRUCTIONRPT", jasperSubReport2);

        	params.put("REPORT_FILE_RESOLVER", new SimpleFileResolver(reportsDir));
        	params.put("REPORT_CONNECTION", connection);
        	BigDecimal bd = new BigDecimal(1124);
        	params.put("REPORTID", bd);
        	
        	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
        	
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
	}*/

	
	
	/*@RequestMapping(value = "/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model) {
		logger.info("getTestpage");

		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		model.addAttribute("recipe", recipe);
		
		return "testpage";
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.info("getStartpage");

		return "start";
	}*/

/*Date date = new Date();
DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
String formattedDate = dateFormat.format(date);
model.addAttribute("serverTime", formattedDate);

Date createTime = new Date(session.getCreationTime());
Date lastAccess = new Date(session.getLastAccessedTime());
int maxInactive = session.getMaxInactiveInterval();
String sess = session.toString();
String sessID = session.getId();

String sCreate = "Session created on: " + createTime;
String sLast = "Session last accessed on: " + lastAccess;
String sInactive = "Session expires after: " + maxInactive + " seconds";
String sID = "Session ID: " + sessID;

String country = locale.getCountry();
String language = locale.getLanguage();
String sCountry = "Request country: " + country;
String sLanguage = "Request language: " + language;

model.addAttribute("create", sCreate);
model.addAttribute("last", sLast);
model.addAttribute("inactive", sInactive);
model.addAttribute("sess", sess);
model.addAttribute("sessID", sID);
model.addAttribute("country", sCountry);
model.addAttribute("language", sLanguage);*/

/*	session registry stuff...
Date createTime = new Date(session.getCreationTime());
Date lastAccess = new Date(session.getLastAccessedTime());
int maxInactive = session.getMaxInactiveInterval();
String sessID = session.getId();

logger.info("Session created on: " + createTime);
logger.info("Session last accessed on: " + lastAccess);
logger.info("Session expires after: " + maxInactive + " seconds");
logger.info("Session ID: " + sessID);

List<Object> allPrinc = sessionRegistry.getAllPrincipals();

for (Object obj : allPrinc) {
	final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

	for (SessionInformation sess : sessions) {
		Object princ = sess.getPrincipal();
		String sessId = sess.getSessionId();
		Date sessDate = sess.getLastRequest();
		
		logger.info("sessionRegistry.princ: " + princ);
		logger.info("sessionRegistry.sessId: " + sessId);
		logger.info("sessionRegistry.sessDate: " + sessDate.toString());
	}
}
*/