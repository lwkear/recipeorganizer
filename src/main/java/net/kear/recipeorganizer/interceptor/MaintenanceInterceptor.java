package net.kear.recipeorganizer.interceptor;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.util.maint.MaintAware;
import net.kear.recipeorganizer.util.maint.MaintenanceProperties;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.zxing.common.detector.MathUtils;

@Component
public class MaintenanceInterceptor extends HandlerInterceptorAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static int DEFAULT_DURATION = 60;			//60 minutes
	private static String DEFAULT_STARTTME = "00:00";	//midnight
	private static int DEFAULT_ALERT = 60 * 60 * 12;	//12 hours
	private static int DEFAULT_WARNING = 60 * 5;		//5 minutes
	private static int EMERGENCY_DELAY = (60 * 5) - 1;	//5 minutes minus 1 second
	
	private List<Integer> daysOfWeek = null;
	private String startHourMinute = DEFAULT_STARTTME;
	private int duration = DEFAULT_DURATION;
	private int emergencyDuration = DEFAULT_DURATION;
	private DateTime startTime;
	private DateTime endTime;
	private boolean maintenanceEnabled = false;
	private boolean maintenanceWindowSet = false;
	private boolean maintenanceInEffect = false;
	private boolean emergencyMaintenance = false;
	private String maintenanceUrl = "";
	
	private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private MaintenanceProperties maintProps;

	@Autowired
	private MessageSource messages;
	@Autowired
	private SessionRegistry sessionRegistry;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!maintenanceEnabled && !emergencyMaintenance)
			return true;

		HandlerMethod handlerMethod = (HandlerMethod)handler;
		Method method = handlerMethod.getMethod();
		ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
		if (responseBody != null)
			return true;
		
		if (startTime != null && endTime != null) {
			DateTime now = new DateTime();
			if (now.isAfter(startTime) && now.isBefore(endTime)) {
				String requestURI = request.getRequestURI();
				if (maintenanceInEffect && requestURI.indexOf(maintenanceUrl) >= 0)
					return true;
				maintenanceInEffect = true;
				logger.debug("maintenance window in effect");
				redirectStrategy.sendRedirect(request, response, maintenanceUrl);
				return false;
			}
			if (now.isAfter(endTime)) {
				logger.debug("maintenance window ended");
				maintenanceInEffect = false;
				emergencyMaintenance = false;
				setNextWindow();
			}
		}
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		HandlerMethod handlerMethod = (HandlerMethod)handler;
		Method method = handlerMethod.getMethod();
		MaintAware maintAware = method.getAnnotation(MaintAware.class);
		if (maintAware != null) {
			Locale locale = request.getLocale();
			if (isMaintenanceWindowSet() && !isMaintenanceInEffect()) {
				String msg = getImminentMaint(locale);
				if (!msg.isEmpty())
					modelAndView.addObject("warningMaint", msg);
			}
		}
		
		super.postHandle(request, response, handler, modelAndView);
	}

	public void setMaintenanceUrl(String maintenanceUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(maintenanceUrl), "url must start with '/' or with 'http(s)'");
		this.maintenanceUrl = maintenanceUrl;
		this.maintenanceInEffect = false;
		this.emergencyMaintenance = false;
		this.maintenanceEnabled = false;
	}
	
	public void setMaintProperties(MaintenanceProperties maintProps) {
		Assert.isTrue(maintProps != null, "MaintenancePropties cannot be null");
		this.maintProps = maintProps;
	}
	
	public void initializeSettings() {
		if (maintProps.getPropertiesConfig()) {
			this.maintenanceEnabled = maintProps.getBooleanProperty(MaintenanceProperties.ENABLED_PROP);
			this.daysOfWeek = maintProps.getIntegerArrayProperty(MaintenanceProperties.DAYS_OF_WEEK_PROP);
			this.startHourMinute = maintProps.getStringProperty(MaintenanceProperties.START_TIME_PROP);
			this.duration = maintProps.getIntegerProperty(MaintenanceProperties.DURATION_PROP);
			
			if (this.startHourMinute.isEmpty())
				this.startHourMinute = DEFAULT_STARTTME;
			if (this.duration == 0)
				this.duration = DEFAULT_DURATION;
		}
	}

	public boolean refreshSettings(MaintenanceDto maintDto) {
		if (!maintProps.updateProperties(maintDto))
			return false;
		
		this.maintenanceEnabled = maintProps.getBooleanProperty(MaintenanceProperties.ENABLED_PROP);
		this.daysOfWeek = maintProps.getIntegerArrayProperty(MaintenanceProperties.DAYS_OF_WEEK_PROP);
		this.startHourMinute = maintProps.getStringProperty(MaintenanceProperties.START_TIME_PROP);
		this.duration = maintProps.getIntegerProperty(MaintenanceProperties.DURATION_PROP);
		this.emergencyMaintenance = maintDto.isEmergencyMaintenance();
		this.emergencyDuration = maintDto.getEmergencyDuration();
		
		return true;
	}
	
	public List<Integer> getDaysOfWeek() {
		return this.daysOfWeek;
	}
	
	public String getStartHourMinute() {
		return this.startHourMinute;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getEmergencyDuration() {
		return this.emergencyDuration;
	}
	
	public DateTime getStartTime() {
		return this.startTime;
	}

	public DateTime getEndTime() {
		return this.endTime;
	}

	public boolean isMaintenanceEnabled() {
		return this.maintenanceEnabled;
	}

	public boolean isEmergencyMaintenance() {
		return this.emergencyMaintenance;
	}

	public boolean isMaintenanceWindowSet() {
		return this.maintenanceWindowSet;
	}
	
	public boolean isMaintenanceInEffect() {
		return this.maintenanceInEffect;
	}

	public void setNextWindow() {
		if (!maintenanceEnabled && !emergencyMaintenance) {
			logger.debug("Maintenance not enabled");
			return;
		}
		
		if (emergencyMaintenance) {
			setEmergencyWindow();
			return;
		}
		
		if (daysOfWeek == null || daysOfWeek.isEmpty()) {
			startTime = null;
			endTime = null;
			maintenanceWindowSet = false;
			logger.debug("No maintenance window set");
			return;
		}
			
		DateTime now = new DateTime();
		int today = now.getDayOfWeek();
		
		String hour = startHourMinute.split(":")[0];
		String minute = startHourMinute.split(":")[1];
		int hr = Integer.parseInt(hour);
		int min = Integer.parseInt(minute);
				
		//check to see if the startHour has already passed; increment today if it has
		DateTime start = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hr, min);
		if (now.isAfter(start))
			today++;
		
		//loop through the daysOfWeek array looking for the first day after today
		int nextDay = 0;
		for (int index = today;index < 8;index++) {
			for (int ndx = 0;ndx < daysOfWeek.size();ndx++) {
				if (daysOfWeek.get(ndx) == index) {
					nextDay = index;
					break;
				}
			}
			if (nextDay > 0)
				break;
		}
		
		//didn't find one after today, so use the first day which will be in next week 
		if (nextDay == 0)
			nextDay = daysOfWeek.get(0);
		
		//refresh what day it is
		today = now.getDayOfWeek();
		//calc how many days from now the next startTime is		
		int numDays;
		if (nextDay >= today)
			numDays = nextDay - today;
		else
			numDays = (7-today) + nextDay;

		startTime = start.plusDays(numDays);
		if (now.isAfter(startTime))
			startTime = start.plusDays(7);
		endTime = startTime.plusMinutes(duration);
		maintenanceWindowSet = true;
		logger.debug("next Window start/end: " + startTime.toString() + " / " + endTime.toString());
	}

	public void setEmergencyWindow() {
		startTime = new DateTime();
		//set the time for 5 minutes (minus 1 second) from now
		startTime = startTime.plusSeconds(EMERGENCY_DELAY);
		endTime = startTime.plusMinutes(duration);
		maintenanceWindowSet = true;
		logger.debug("emergency Window start/end: " + startTime.toString() + " / " + endTime.toString());
	}
	
	public String getNextStartWindow(boolean user, String msgCode, Locale locale) {
		logger.debug("getNextStartWindow");
		
		String nextWindow = "";

		//members only see a message on the dashboard
		//admins need to see the next window on the System Maintenance page
		if (user) {
			if (!maintenanceEnabled && !maintenanceWindowSet)
				return nextWindow;
			
			DateTime now = new DateTime();
			
			int seconds = Seconds.secondsBetween(now, startTime).getSeconds();
			
			logger.debug("getNextStartWindow: now/startTime= " + now.toString() + " / " + startTime.toString());
			logger.debug("getNextStartWindow: seconds timeDiff= " + seconds);
			
			//do not display a message is maintenance is more than 24 hours from now or less than 5 minutes
			//emergency maintenance always starts in 5 minutes so no need to check here - an imminent message will be displayed instead
			if ((seconds > DEFAULT_ALERT) || (seconds < DEFAULT_WARNING) || (seconds < 0))
				return nextWindow;
		}

		if (!maintenanceEnabled && !emergencyMaintenance) {
			nextWindow = messages.getMessage("sysmaint.nomaintenance", null, "", locale);			
			return nextWindow;
		}
		
		if (!maintenanceWindowSet) {
			nextWindow = messages.getMessage("sysmaint.nowindow", null, "", locale);
			return nextWindow;
		}		
		
		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
		Date dt = new Date(this.startTime.getMillis());
		String startDate = dateFormatter.format(dt); 
		
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, locale);
		String startTime = timeFormatter.format(dt);
		dt = new Date(this.endTime.getMillis());
		String endTime = timeFormatter.format(dt);

		Object[] args = new Object[] {startDate, startTime, endTime};
		nextWindow = messages.getMessage(msgCode, args, "", locale);
		return nextWindow;
	}
	
	public String getImminentMaint(Locale locale) {
		logger.debug("getImminentMaint");
		
		String imminentMsg = "";

		if (!maintenanceWindowSet)
			return imminentMsg;
		
		DateTime now = new DateTime();
		int seconds = Seconds.secondsBetween(now, startTime).getSeconds();		
		
		logger.debug("getImminentMaint: now/startTime= " + now.toString() + " / " + startTime.toString());
		
		if ((seconds > DEFAULT_WARNING) || (now.isAfter(startTime)) || (seconds < 0))
			return imminentMsg;
		int minutes = MathUtils.round(seconds / 60);
		
		logger.debug("getImminentMaint: seconds / round(seconds/60)= " + seconds  + " / " + minutes);
		
		//always round up
		minutes += 1;
		
		Object[] args = new Object[] {minutes, minutes > 1 ? "s" : "", this.duration};		
		
		String msgCode = "sysmaint.imminent";
		if (emergencyMaintenance)
			msgCode = "sysmaint.emergency";
		
		imminentMsg = messages.getMessage(msgCode, args, "", locale);
		return imminentMsg;
	}

	public String getMaintEnd(Locale locale) {
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, locale);
		Date dt = new Date(this.endTime.getMillis());
		String endTime = timeFormatter.format(dt);

		Object[] args = new Object[] {endTime};
		String maintEnd = messages.getMessage("sysmaint.message2", args, "", locale);
		return maintEnd;
	}
}
