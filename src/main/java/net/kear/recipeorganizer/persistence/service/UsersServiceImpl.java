package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Users;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.repository.RoleRepository;
import net.kear.recipeorganizer.persistence.repository.UsersRepository;
import net.kear.recipeorganizer.persistence.repository.VerificationTokenRepository;
import net.kear.recipeorganizer.persistence.service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class UsersServiceImpl implements UsersService {
 
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    public void addUser(Users user) {
    	//Note: by this point the initial validation of password=confirmPassword has been completed;
    	//	however, Spring or Hibernate appear to perform the validation again so the encoded password
    	//	must be copied to confirmPassword; an alternative would be to use a DTO for sign-up purposes,
    	//	copying the appropriate fields to the USER model object 
    	String encodedPsswd = passwordEncoder.encode(user.getPassword()); 
    	user.setPassword(encodedPsswd);
    	user.setConfirmPassword(encodedPsswd);
    	user.setEnabled(0);
    	user.setTokenExpired(0);
    	user.setRole(roleRepository.getDefaultRole());
    	usersRepository.addUser(user);
    }
    
    public void updateUser(Users user) {
    	usersRepository.updateUser(user);
    }
 
    public void deleteUser(Long id) {
    	usersRepository.deleteUser(id);
    }

    public Users findUserByEmail(String email) {
    	return usersRepository.findUserByEmail(email);
    }
    
    public Boolean doesUserEmailExist(String email) {
    	return usersRepository.doesUserEmailExist(email);
    }
    
    public Boolean validateUser(String email, String password) {
    	return usersRepository.validateUser(email, password);
    }
    
	public List<Users> listUsers() {
    	return usersRepository.listUsers();
    }
    
    public String getUserName(Long id) {
    	return usersRepository.getUserName(id);
    }
    
    public Users getUser(Long id) {
    	return usersRepository.getUser(id);
    }

    @Override
    public void createUserVerificationToken(final Users user, final String token) {
        final VerificationToken newToken = new VerificationToken(token, user);
        tokenRepository.saveToken(newToken);
    }
}