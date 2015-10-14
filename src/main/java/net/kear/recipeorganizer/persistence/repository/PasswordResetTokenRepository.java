package net.kear.recipeorganizer.persistence.repository;

import net.kear.recipeorganizer.persistence.model.PasswordResetToken;

public interface PasswordResetTokenRepository {

	public void saveToken(PasswordResetToken token);
	public PasswordResetToken findByToken(String token);
}
