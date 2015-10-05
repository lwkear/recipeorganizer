package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Users;
 
public interface UsersService {
     
    public void addUser(Users users);
    public void updateUser(Users users);
    public void deleteUser(Long id);
    public Users findUserByEmail(String email);
    public Boolean doesUserEmailExist(String email);
    public Boolean validateUser(String email, String password);
    public List<Users> listUsers();
    public String getUserName(Long id);
    public Users getUser(Long id);
    public void createUserVerificationToken(final Users user, final String token);
}