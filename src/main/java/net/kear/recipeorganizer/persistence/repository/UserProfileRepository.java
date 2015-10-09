package net.kear.recipeorganizer.persistence.repository;

import net.kear.recipeorganizer.persistence.model.UserProfile;

public interface UserProfileRepository {

	public void addUserProfile(UserProfile userProfile);
	public void updateUserProfile(UserProfile userProfile);

}
