package it.collegio.models;



public class Committente {
    
    private int id;
    private String nome;
    private User admin;
    private String email;
    private String mobile;
    private int id_address;
    
    //Costruttori
    public Committente(int id, String nome, User admin, String email, String mobile, int id_address){
    
        super();
        
        this.id = id;
        this.nome = nome;
        this.admin = admin;
        this.email = email;
        this.mobile = mobile;
        this.id_address = id_address;
        
    }
    
    public Committente(){
    
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
     * @return the admin
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(User admin) {
        this.admin = admin;
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
    
    
}
