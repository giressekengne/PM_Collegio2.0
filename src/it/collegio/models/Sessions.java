
package it.collegio.models;

import it.collegio.enums.RoomType;
import it.collegio.enums.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class Sessions {
    
    private int id;
    private String user;
    private int id_committente;
    private int id_role;
    private String token;
    private LocalDateTime expired;
    private LocalDateTime dataCreazione;
    
    public Sessions(){}

    public Sessions(int id, String user, int id_committente, int id_role, String token, LocalDateTime expired, LocalDateTime dataCreazione) {
        this.id = id;
        this.user = user;
        this.id_committente = id_committente;
        this.id_role = id_role;
        this.token = token;
        this.expired = expired;
        this.dataCreazione = dataCreazione;
    }

    public Sessions(String user, int id_committente, int id_role, String token, LocalDateTime expired, LocalDateTime dataCreazione) {
        this.user = user;
        this.id_committente = id_committente;
        this.id_role = id_role;
        this.token = token;
        this.expired = expired;
        this.dataCreazione = dataCreazione;
    }
    
    

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the utente
     */
    public String getUser() {
        return user;
    }

    /**
     * @param utente the utente to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the committente
     */
    public int getCommittente() {
        return id_committente;
    }

    /**
     * @param committente the committente to set
     */
    public void setCommittente(int id_committente) {
        this.id_committente = id_committente;
    }

    /**
     * @return the role
     */
    public int getRole() {
        return id_role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(int id_role) {
        this.id_role = id_role;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the expired
     */
    public LocalDateTime getExpired() {
        return expired;
    }

    /**
     * @param expired the expired to set
     */
    public void setExpired(LocalDateTime expired) {
        this.expired = expired;
    }

    /**
     * @return the dataCreazione
     */
    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    /**
     * @param dataCreazione the dataCreazione to set
     */
    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    
    

    
    
}
