package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.UUID;

import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.repository.PasswordResetTokenRepository;
import net.kear.recipeorganizer.persistence.repository.RoleRepository;
import net.kear.recipeorganizer.persistence.repository.UserProfileRepository;
import net.kear.recipeorganizer.persistence.repository.UserRepository;
import net.kear.recipeorganizer.persistence.repository.VerificationTokenRepository;
import net.kear.recipeorganizer.persistence.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class UserServiceImpl implements UserService {
 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    public User addUser(UserDto userDto) {
    	User user = new User();
    	
    	user.setEmail(userDto.getEmail());
    	String encodedPsswd = passwordEncoder.encode(userDto.getPassword()); 
    	user.setPassword(encodedPsswd);
    	user.setFirstName(userDto.getFirstName());
    	user.setLastName(userDto.getLastName());
    	user.setEnabled(0);
    	user.setTokenExpired(0);
    	user.setLocked(0);
    	user.setAccountExpired(0);
    	user.setPasswordExpired(0);
    	if (userDto.getSubmitRecipes()) {
    		Role role = roleRepository.getRole("AUTHOR");
    		if (role != null)
    			user.setRole(role);
    		else
    			user.setRole(roleRepository.getDefaultRole());
    	}
    	else
    		user.setRole(roleRepository.getDefaultRole());
    	userRepository.addUser(user);
    	
    	return user;
    }
    
    public void updateUser(User user) {
    	userRepository.updateUser(user);
    }
 
    public void deleteUser(Long id) {
    	userRepository.deleteUser(id);
    }
    
	public List<User> getUsers() {
    	return userRepository.getUsers();
    }
    
    public User getUser(Long id) {
    	return userRepository.getUser(id);
    }
    
    public User getVerificationUser(final String token) {
        final User user = verificationTokenRepository.findByToken(token).getUser();
        return user;
    }
    
    public User getPasswordResetUser(final String token) {
        final User user = passwordResetTokenRepository.findByToken(token).getUser();
        return user;
    }
    
    public User getUserWithProfile(Long id) {
    	return userRepository.getUserWithProfile(id);
    }
    
    public String getUserFullName(Long id) {
    	return userRepository.getUserFullName(id);
    }
    
    public User findUserByEmail(String email) {
    	return userRepository.findUserByEmail(email);
    }
    
    public boolean doesUserEmailExist(String email) {
    	return userRepository.doesUserEmailExist(email);
    }
    
    public boolean validateUser(String email, String password) {
    	return userRepository.validateUser(email, password);
    }
    
    public boolean isPasswordValid(String password, User user) {
    	return passwordEncoder.matches(password, user.getPassword());
    }
    
    public void changePassword(String password, User user) {
    	user.setPassword(passwordEncoder.encode(password));
    	userRepository.updateUser(user);  	
    }

    public void saveUserProfile(UserProfile userProfile) {
    	if (userProfile.getId() == 0)
    	   	userProfileRepository.addUserProfile(userProfile);
    	else
    		userProfileRepository.updateUserProfile(userProfile);
    }
    
    @Override
    public void createUserVerificationToken(final User user, final String token) {
        final VerificationToken newToken = new VerificationToken(token, user);
        verificationTokenRepository.saveToken(newToken);
    }
    
    public VerificationToken recreateUserVerificationToken(String token) {
	    VerificationToken newToken = verificationTokenRepository.findByToken(token);
	    newToken.updateToken(UUID.randomUUID().toString());
	    verificationTokenRepository.saveToken(newToken);
	    return newToken;
    }
    
    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
    
    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken newToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.saveToken(newToken);
    }
    
    @Override
    public PasswordResetToken recreatePasswordResetTokenForUser(String token) {
    	PasswordResetToken newToken = passwordResetTokenRepository.findByToken(token);
	    newToken.updateToken(UUID.randomUUID().toString());
	    passwordResetTokenRepository.saveToken(newToken);
	    return newToken;
    }    
    
    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
    	return passwordResetTokenRepository.findByToken(token);
    }
}