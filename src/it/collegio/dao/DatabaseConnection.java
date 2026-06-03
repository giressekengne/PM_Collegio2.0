package it.collegio.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/pm_collegiov2";     
    private static final String USER = "root"; 
    private static final String PASSWORD = "Giresse98!";
    
    private Connection connection;
    private static DatabaseConnection dbConnection = null;
    
    private DatabaseConnection(){
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DatabaseConnection getDatabaseConnection(){
       if(dbConnection == null){
           dbConnection = new DatabaseConnection();
       }
        return dbConnection;
                
    }

    public Connection getConnection() {
        return connection;
    }
    
    
    
}
    
    
    

