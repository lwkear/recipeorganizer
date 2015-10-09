package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.model.User;
 
public interface UserRepository {

    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(Long id);
    public User findUserByEmail(String email);
    public boolean doesUserEmailExist(String email);
    public boolean validateUser(String email, String password);
    public List<User> listUsers();
    public String getUserFullName(Long id);
    public User getUser(Long id);
    public User getUserWithProfile(Long id);
}
