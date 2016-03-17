package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Min;

import net.kear.recipeorganizer.validation.MaintStartTime;

public class MaintenanceDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private List<Integer> daysOfWeek;
	
	@Min(value=1)
	private int duration;
	
	@MaintStartTime	
	private String startTime;

	private boolean maintenanceEnabled = false;
	private boolean emergencyMaintenance = false;
	private int emergencyDuration = 0;
	
	public MaintenanceDto() {}

	public MaintenanceDto(List<Integer> daysOfWeek, int duration, String startTime) {
		super();
		this.daysOfWeek = daysOfWeek;
		this.duration = duration;
		this.startTime = startTime;
	}

	public List<Integer> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Integer> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public boolean isMaintenanceEnabled() {
		return maintenanceEnabled;
	}
	
	public void setMaintenanceEnabled(boolean maintenanceEnabled) {
		this.maintenanceEnabled = maintenanceEnabled;
	}

	public boolean isEmergencyMaintenance() {
		return emergencyMaintenance;
	}

	public void setEmergencyMaintenance(boolean emergencyMaintenance) {
		this.emergencyMaintenance = emergencyMaintenance;
	}

	public int getEmergencyDuration() {
		return emergencyDuration;
	}

	public void setEmergencyDuration(int emergencyDuration) {
		this.emergencyDuration = emergencyDuration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((daysOfWeek == null) ? 0 : daysOfWeek.hashCode());
		result = prime * result + duration;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaintenanceDto other = (MaintenanceDto) obj;
		if (daysOfWeek == null) {
			if (other.daysOfWeek != null)
				return false;
		} else if (!daysOfWeek.equals(other.daysOfWeek))
			return false;
		if (duration != other.duration)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaintenanceDto [daysOfWeek=" + daysOfWeek 
				+ ", duration=" + duration 
				+ ", startTime=" + startTime + "]"; 
	}
}
