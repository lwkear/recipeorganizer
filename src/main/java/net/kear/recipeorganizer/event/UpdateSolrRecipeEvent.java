package net.kear.recipeorganizer.event;

import net.kear.recipeorganizer.persistence.model.Recipe;

public class UpdateSolrRecipeEvent {

	Recipe recipe;
	boolean deleteFirst;
	boolean deleteOnly;
	
	public UpdateSolrRecipeEvent(Recipe recipe) {
		this.recipe = recipe;
		this.deleteFirst = false;
		this.deleteOnly = false;
	}

	public UpdateSolrRecipeEvent(Recipe recipe, boolean deleteFirst) {
		this.recipe = recipe;
		this.deleteFirst = deleteFirst;
		this.deleteOnly = false;
	}

	public UpdateSolrRecipeEvent(Recipe recipe, boolean deleteFirst, boolean deleteOnly) {
		this.recipe = recipe;
		this.deleteFirst = deleteFirst;
		this.deleteOnly = deleteOnly;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public boolean isDeleteFirst() {
		return deleteFirst;
	}

	public boolean isDeleteOnly() {
		return deleteOnly;
	}
}
