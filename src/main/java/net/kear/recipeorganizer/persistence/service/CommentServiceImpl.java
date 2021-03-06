package net.kear.recipeorganizer.persistence.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.FlaggedCommentDto;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
import net.kear.recipeorganizer.persistence.repository.CommentRepository;
 
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    public void addComment(RecipeComment recipeComment) {
    	recipeComment.setFlag(0);
    	commentRepository.addComment(recipeComment);
    }
    
    public void deleteComment(long id) {
    	commentRepository.deleteComment(id);
    }
    
    public List<CommentDto> listComments(long recipeId) {
    	return commentRepository.listComments(recipeId);
    }
    
    public long getCommentCount(long recipeId) {
    	return commentRepository.getCommentCount(recipeId);
    }
    
    public void setCommentFlag(long id, int flag) {
    	commentRepository.setCommentFlag(id, flag);
    }

    public Long getFlaggedCount() {
    	return commentRepository.getFlaggedCount();
    }
    
    public List<FlaggedCommentDto> getFlaggedComments() {
    	return commentRepository.getFlaggedComments();
    }
}
