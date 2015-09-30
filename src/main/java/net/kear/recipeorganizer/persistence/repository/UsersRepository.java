package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.model.Users;
 
public interface UsersRepository {

    public void addUser(Users users);
    public void updateUser(Users users);
    public void deleteUser(Long id);
    public Users findUserByEmail(String email);
    public boolean doesUserEmailExist(String email);
    public boolean validateUser(String email, String password);
    public List<Users> listUsers();
    public String getUserName(Long id);
    public Users getUser(Long id);
    
}
