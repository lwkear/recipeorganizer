package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.ChangeNotificationDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
 
public interface UserService {
     
    public User addUser(UserDto userDto);
    public void updateUser(User user);
    public void deleteUser(Long id);
    public List<User> getUsers();
    public User getUser(Long id);
    public User getVerificationUser(final String token);
    public User getPasswordResetUser(final String token);
    public User getUserWithProfile(Long id);
    public String getUserFullName(Long id);
    public User findUserByEmail(String email);
    public boolean doesUserEmailExist(String email);
    public boolean validateUser(String email, String password);
    public boolean isPasswordValid(String password, User user);
    public User changeName(String firstName, String lastName, User user);
    public User changeEmail(String email, User user);
    public User changePassword(String password, User user);
    public User changeNotification(boolean emailAdmin, boolean emailRecipe, boolean emailMessage, User user);
    public User changeNotification(ChangeNotificationDto changeNotificationDto, User user);
    public void changeRole(String roleName, User user);
    public void saveUserProfile(UserProfile userProfile);
    public void createUserVerificationToken(User user, String token);
    public VerificationToken recreateUserVerificationToken(String token);
    public VerificationToken getVerificationToken(String token);
    public void deleteVerificationToken(VerificationToken token);
    public void createPasswordResetTokenForUser(final User user, final String token);
    public PasswordResetToken recreatePasswordResetTokenForUser(final String token);
    public PasswordResetToken getPasswordResetToken(String token);
    public void deletePasswordResetToken(long usserId);
    public void setLastLogin(User user);
}