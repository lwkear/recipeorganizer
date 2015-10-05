package net.kear.recipeorganizer.persistence.repository;

import net.kear.recipeorganizer.persistence.model.VerificationToken;

public interface VerificationTokenRepository {

	public void saveToken(VerificationToken token);
}
