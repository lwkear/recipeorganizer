package net.kear.recipeorganizer.persistence.service;

public interface DatabaseStatusService {

	public void checkStatus();
	public boolean isDatabaseAccessible();
	public void setDatabaseStatus(boolean status);
}
