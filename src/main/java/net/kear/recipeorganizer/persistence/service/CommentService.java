package net.kear.recipeorganizer.persistence.service;

import java.util.List;

import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
 
public interface CommentService {

    public void addComment(RecipeComment recipeComment);
    public void deleteComment(long id);
    public List<CommentDto> listComments(long recipeId);
    public long getCommentCount(long recipeId);
    public void setCommentFlag(long id, int flag);
}
