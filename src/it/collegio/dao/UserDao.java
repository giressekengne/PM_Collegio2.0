package it.collegio.dao;

import it.collegio.enums.Genere;
import it.collegio.enums.UserStatus;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.collegio.models.User;
import it.collegio.utilities.QueryContainer;

public class UserDao {
    
    
    private DatabaseConnection dbConnection;

    public UserDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }
     
    public boolean ValidateUser(User user){
         
        try {
            String query = QueryContainer.queryLogin;
            PreparedStatement pst= null;
            pst=this.dbConnection.getConnection().prepareStatement(query);
            pst.setString(1,user.getEmail());
            pst.setString(2, user.getPw());
            ResultSet rs=pst.executeQuery();
            
            if(rs.next()){
                return true;
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    } 
    
    

    
    public User getUser(String email){
        return findUser(QueryContainer.queryGetUserByMail, email);
    }

    public User getByCounter(String userCounter){
        return findUser(QueryContainer.queryGetUserByCounter, userCounter);
    }

    private User findUser(String query, String param){
        User user = null;

        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, param);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setCounter(rs.getString("user_counter"));
                    user.setNome(rs.getString("nome"));
                    user.setCognome(rs.getString("cognome"));
                    user.setEmail(rs.getString("email"));
                    user.setPw(rs.getString("access"));
                    user.setRole(rs.getInt("ruolo"));
                    user.setCommittente(rs.getInt("committente_id"));
                    user.setStato(UserStatus.valueOf(rs.getString("stato").toUpperCase()));
                    user.setMobile(rs.getString("telefono"));
                    user.setAddress(rs.getInt("indirizzo_id"));
                    user.setRecupero(rs.getString("recupero"));
                    user.setResponse(rs.getString("response"));
                    user.setGenere(Genere.valueOf(rs.getString("genere").toUpperCase()));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user;
    }
    
    private boolean hasRoleType(User user, String roleType){

        try {
            String query = QueryContainer.queryGetRoleById;
            PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(query);
            pst.setInt(1, user.getRole());
            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                return roleType.equals(rs.getString("role_type"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isAdminSis(User user){
        return hasRoleType(user, "AS");
    }

    public boolean isAdminCom(User user){
        return hasRoleType(user, "AC");
    }

    public boolean isAdmin(User user){
        return hasRoleType(user, "AR");
    }
    
    public java.util.List<User> getAll(){
        java.util.List<User> users = new java.util.ArrayList<>();
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryUsers);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    public java.util.List<User> getByCommittente(int committenteId){
        java.util.List<User> users = new java.util.ArrayList<>();
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryUsersByCom)) {
            pst.setInt(1, committenteId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }

    public boolean existsByEmail(String email){
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryGetUserByMail)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean existsByCounter(String counter){
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryGetUserByCounter)) {
            pst.setString(1, counter);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public java.util.List<String> getEmailsByRoleNotIn(int excludedRole){
        java.util.List<String> emails = new java.util.ArrayList<>();
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryGetAdminEmails)) {
            pst.setInt(1, excludedRole);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    emails.add(rs.getString("email"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return emails;
    }

    public int countUsers(){
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryCountUsers);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean updatePasswordByEmail(String email, String newPassword){
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryUpdPasswordByEmail)) {
            pst.setString(1, newPassword);
            pst.setString(2, email);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Genera uno user_counter univoco con pattern "XXX{id}{NOME}".
     * id parte da countUsers()+1; in caso di collisione (utente cancellato
     * e ricreato) incrementa fino a trovare uno libero. Max 100 retry.
     */
    public String generateUniqueCounter(String nome){
        if (nome == null || nome.isEmpty()) {
            return null;
        }
        String nomeUpper = nome.trim().toUpperCase().replaceAll("\\s+", "");
        int baseId = countUsers() + 1;
        for (int i = 0; i < 100; i++) {
            String candidate = "XXX" + (baseId + i) + nomeUpper;
            if (!existsByCounter(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public boolean deleteUser(String counter){
        try (PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(QueryContainer.queryDelUser)) {
            pst.setString(1, counter);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setCounter(rs.getString("user_counter"));
        user.setNome(rs.getString("nome"));
        user.setCognome(rs.getString("cognome"));
        user.setEmail(rs.getString("email"));
        user.setPw(rs.getString("access"));
        user.setRole(rs.getInt("ruolo"));
        user.setCommittente(rs.getInt("committente_id"));
        user.setStato(UserStatus.valueOf(rs.getString("stato").toUpperCase()));
        user.setMobile(rs.getString("telefono"));
        user.setAddress(rs.getInt("indirizzo_id"));
        user.setRecupero(rs.getString("recupero"));
        user.setResponse(rs.getString("response"));
        user.setGenere(Genere.valueOf(rs.getString("genere").toUpperCase()));
        return user;
    }

    public boolean insertUser(User user){

        String query = QueryContainer.queryInsUser;

    try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setString(1, user.getCounter().toUpperCase());
            pst.setString(2, user.getNome());
            pst.setString(3, user.getCognome());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPw());
            pst.setInt(6, user.getRole());
            pst.setInt(7, user.getCommittente());
            pst.setString(8, user.getStato().name().toLowerCase());
            pst.setString(9, user.getMobile());
            pst.setInt(10, user.getAddress());
            pst.setString(11, user.getRecupero());
            pst.setString(12, user.getResponse());
            pst.setString(13, user.getGenere().name().toLowerCase());
           
        int rows = pst.executeUpdate();  

        return rows > 0;   

    } catch (SQLException ex) {
        Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex); 
    }
        return false;
    
    }
    
    public boolean UpdateUser(User user){

        String query = QueryContainer.queryUpdUser;

    try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setString(1, user.getNome());
            pst.setString(2, user.getCognome());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getPw());
            pst.setInt(5, user.getRole());
            pst.setInt(6, user.getCommittente());
            pst.setString(7, user.getStato().name().toLowerCase());
            pst.setString(8, user.getMobile());
            pst.setInt(9, user.getAddress());
            pst.setString(10, user.getRecupero());
            pst.setString(11, user.getResponse());
            pst.setString(12, user.getGenere().name().toLowerCase());
            pst.setString(13, user.getCounter().toUpperCase());
            
           
        int rows = pst.executeUpdate();  

        return rows > 0;   

    } catch (SQLException ex) {
        Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex); 
    }
        return false;
    
    }
    
}
