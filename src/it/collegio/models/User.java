package it.collegio.models;

import it.collegio.enums.UserStatus;
import it.collegio.enums.Genere;

//@author gigatore
public class User {
    
    private String counter;
    private String nome;
    private String cognome;
    private String email;
    private String pw;
    private int id_role;
    private int id_committente;
    private UserStatus stato;
    private String mobile;
    private int  id_address;
    private String recupero;
    private String response;
    private Genere genere;
    
    
    
    //costruttori
    public User(String counter, String nome, String cognome, String email, String pw, int id_role,
            int id_committente, UserStatus stato, String mobile, int id_address, String recupero,
            String response, Genere genere){
 
        super();
        
        this.counter = counter;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.pw = pw;
        this.id_role = id_role;
        this.id_committente = id_committente;
        this.stato = stato;
        this.mobile = mobile;
        this.id_address = id_address;
        this.recupero = recupero;
        this.response = response;
        this.genere = genere;
    }
    
    public User( String email, String pw, UserStatus stato,  String recupero, String response){
 
        super();
      
        this.email = email;
        this.pw = pw;
        this.stato = stato;
        this.recupero = recupero;
        this.response = response;
        
    }
    public User( String email, String pw, UserStatus stato, int id_role){
 
        super();
      
        this.email = email;
        this.pw = pw;
        this.stato = stato;
        this.id_role = id_role;
        
        
    }
    
    public User( String email, String pw){
 
        super();
      
        this.email = email;
        this.pw = pw;
            
    }
    
    public User( String counter){
 
        super();
      
        this.counter = counter;
            
    }
    
    public User(){
    
    }

    /**
     * @return the counter
     */
    public String getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(String counter) {
        this.counter = counter;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the cognome
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome the cognome to set
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the pw
     */
    public String getPw() {
        return pw;
    }

    /**
     * @param pw the pw to set
     */
    public void setPw(String pw) {
        this.pw = pw;
    }

    /**
     * @return the ruolo
     */
    public int getRole() {
        return id_role;
    }

    /**
     * @param ruolo the ruolo to set
     */
    public void setRole(int id_role) {
        this.id_role = id_role;
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
     * @return the stato
     */
    public UserStatus getStato() {
        return stato;
    }

    /**
     * @param stato the stato to set
     */
    public void setStato(UserStatus stato) {
        this.stato = stato;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the address
     */
    public int getAddress() {
        return id_address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(int id_address) {
        this.id_address = id_address;
    }

    /**
     * @return the recupero
     */
    public String getRecupero() {
        return recupero;
    }

    /**
     * @param recupero the recupero to set
     */
    public void setRecupero(String recupero) {
        this.recupero = recupero;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * @return the genere
     */
    public Genere getGenere() {
        return genere;
    }

    /**
     * @param genere the genere to set
     */
    public void setGenere(Genere genere) {
        this.genere = genere;
    }
}
