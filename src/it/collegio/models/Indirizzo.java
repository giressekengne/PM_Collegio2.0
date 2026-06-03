package it.collegio.models;


// @author gigatore
public class Indirizzo {
    
    private int id;
    private String paese;
    private String provincia;
    private String citta;
    private String via;
    private int cap;
    
    
    //Costruttori
    public Indirizzo(int id, String paese, String provincia, String citta, String via, int cap) {
    
        super();
        
        this.id = id;
        this.paese = paese;
        this.provincia = provincia;
        this.citta = citta;
        this.via = via;
        this.cap = cap;
        
    }
    
    public Indirizzo(int id,  String via) {
    
        super();
        
        this.id = id;
        this.via = via;
       
        
    }
    
    public Indirizzo(int id) {
    
        super();
        
        this.id = id;
        
    }
    
    public Indirizzo() {
    
        
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
     * @return the paese
     */
    public String getPaese() {
        return paese;
    }

    /**
     * @param paese the paese to set
     */
    public void setPaese(String paese) {
        this.paese = paese;
    }

    /**
     * @return the provincia
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * @param provincia the provincia to set
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * @return the citta
     */
    public String getCitta() {
        return citta;
    }

    /**
     * @param citta the citta to set
     */
    public void setCitta(String citta) {
        this.citta = citta;
    }

    /**
     * @return the via
     */
    public String getVia() {
        return via;
    }

    /**
     * @param via the via to set
     */
    public void setVia(String via) {
        this.via = via;
    }

    /**
     * @return the cap
     */
    public int getCap() {
        return cap;
    }

    /**
     * @param cap the cap to set
     */
    public void setCap(int cap) {
        this.cap = cap;
    }
}
