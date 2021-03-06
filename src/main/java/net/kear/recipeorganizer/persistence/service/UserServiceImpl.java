package net.kear.recipeorganizer.persistence.service;
 
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.dto.ChangeNotificationDto;
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
    
    private static final int INVITATION_EXPIRATION = 60 * 24 * 5;    
    
    public User addUser(UserDto userDto) {
    	User user = new User();
    	
    	user.setEmail(userDto.getEmail());
    	String encodedPsswd = passwordEncoder.encode(userDto.getPassword()); 
    	user.setPassword(encodedPsswd);
    	user.setFirstName(userDto.getFirstName());
    	user.setLastName(userDto.getLastName());
    	user.setEmailAdmin(userDto.isEmailAdmin());
    	user.setEmailRecipe(userDto.isEmailRecipe());
    	user.setEmailMessage(userDto.isEmailMessage());
    	user.setInvited(userDto.isInvited());
    	user.setEnabled(0);
    	user.setTokenExpired(0);
    	user.setLocked(0);
    	user.setAccountExpired(0);
    	user.setPasswordExpired(0);
    	if (!userDto.isInvited())
    		user.setPasswordExpiryDate();
    	else {
    		Date expireDate = user.calculateExpiryDate(INVITATION_EXPIRATION);
    		user.setPasswordExpiryDate(expireDate);
    	}
    	if (userDto.isSubmitRecipes()) {
    		Role role = roleRepository.getRole(Role.TYPE_AUTHOR);
    		if (role != null)
    			user.setRole(role);
    		else
    			user.setRole(roleRepository.getRole(Role.TYPE_DEFAULT));
    	}
    	else
    		user.setRole(roleRepository.getRole(Role.TYPE_DEFAULT));
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
    
    public User changeName(String firstName, String lastName, User user) {
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	return userRepository.updateUser(user);  	
    }

    public User changeEmail(String email, User user) {
    	user.setEmail(email);
    	return userRepository.updateUser(user);  	
    }
    
    public User changePassword(String password, User user) {
    	user.setPassword(passwordEncoder.encode(password));
    	user.setPasswordExpired(0);
    	user.setPasswordExpiryDate();
    	return userRepository.updateUser(user);  	
    }

    public User changeNotification(boolean emailAdmin, boolean emailRecipe, boolean emailMessage, User user) {
    	user.setEmailAdmin(emailAdmin);
    	user.setEmailRecipe(emailRecipe);
    	user.setEmailMessage(emailMessage);
    	return userRepository.updateUser(user);  	
    }

    public User changeNotification(ChangeNotificationDto changeNotificationDto, User user) {
    	user.setEmailAdmin(changeNotificationDto.isEmailAdmin());
    	user.setEmailRecipe(changeNotificationDto.isEmailRecipe());
    	user.setEmailMessage(changeNotificationDto.isEmailMessage());
    	return userRepository.updateUser(user);  	
    }

    public void changeRole(String roleName, User user) {
    	Role role = roleRepository.getRole(roleName);
    	if (role != null) {
    		user.setRole(role);
    		userRepository.updateUser(user);
    	}    		
    }

    public void saveUserProfile(UserProfile userProfile) {
    	if (userProfile.getId() == 0)
    	   	userProfileRepository.addUserProfile(userProfile);
    	else
    		userProfileRepository.updateUserProfile(userProfile);
    }
    
    public void createUserVerificationToken(final User user, final String token) {
        final VerificationToken newToken = new VerificationToken(token, user);
        if (user.isInvited()) {
        	Date expireDate = user.calculateExpiryDate(INVITATION_EXPIRATION);
        	newToken.setExpiryDate(expireDate);
        }
        verificationTokenRepository.saveToken(newToken);
    }
    
    public VerificationToken recreateUserVerificationToken(String token) {
	    VerificationToken newToken = verificationTokenRepository.findByToken(token);
	    newToken.updateToken(UUID.randomUUID().toString());
	    verificationTokenRepository.saveToken(newToken);
	    return newToken;
    }
    
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public void deleteVerificationToken(VerificationToken token) {
        verificationTokenRepository.deleteToken(token);
    }
    
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken newToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.saveToken(newToken);
    }
    
    public PasswordResetToken recreatePasswordResetTokenForUser(String token) {
    	PasswordResetToken newToken = passwordResetTokenRepository.findByToken(token);
	    newToken.updateToken(UUID.randomUUID().toString());
	    passwordResetTokenRepository.saveToken(newToken);
	    return newToken;
    }    
    
    public PasswordResetToken getPasswordResetToken(String token) {
    	return passwordResetTokenRepository.findByToken(token);
    }

    public void deletePasswordResetToken(long userId) {
    	passwordResetTokenRepository.deleteToken(userId);
    }

    public void setLastLogin(User user) {
		Calendar todaysDt = Calendar.getInstance();
		todaysDt.setTimeInMillis(new Date().getTime());
		user.setLastLogin(new Date(todaysDt.getTime().getTime()));
    }
}