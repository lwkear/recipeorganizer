package net.kear.recipeorganizer.util.maint;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MaintenanceUtil {

	public MaintenanceUtil() {}

	public Map<Integer, String> getWeekMap(Locale locale) {
		DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
		String[] dayNames = dfs.getWeekdays();

		Map<Integer, String> dayMap = new HashMap<Integer, String>();

		dayMap.put(1, dayNames[Calendar.MONDAY]);
		dayMap.put(2, dayNames[Calendar.TUESDAY]);
		dayMap.put(3, dayNames[Calendar.WEDNESDAY]);
		dayMap.put(4, dayNames[Calendar.THURSDAY]);
		dayMap.put(5, dayNames[Calendar.FRIDAY]);
		dayMap.put(6, dayNames[Calendar.SATURDAY]);
		dayMap.put(7, dayNames[Calendar.SUNDAY]);
		
		return dayMap;
	}
}
