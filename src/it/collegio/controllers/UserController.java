
package it.collegio.controllers;


import it.collegio.dao.UserDao;
import it.collegio.enums.UserStatus;
import it.collegio.models.User;



public class UserController {
    
    
    private UserDao userDAO = new UserDao();

    public boolean login(String email, String password) {
        User user = new User(email, password);
        return userDAO.ValidateUser(user);
        
    }
    
    public User getUser(String email) {  
        return userDAO.getUser(email);
        
    }
}
