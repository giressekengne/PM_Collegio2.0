package it.collegio.models;


// @author gigatore
public class Mansione {
    private int id;
    private String tipo;
    private String nome;
    
    
    //Costruttori
    public Mansione(int id, String tipo,String nome) { 
        super();
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
    }
    
    public Mansione(int id) { 
        super();
        this.id = id;
     
    }
    
    public Mansione() {   
        
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
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
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
    
}
