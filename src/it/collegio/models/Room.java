package it.collegio.models;

import it.collegio.enums.RoomType;
import it.collegio.enums.RoomStatus;
import it.collegio.enums.BedType;


// @author gigatore
public class Room {

    private int id;
    private int id_committente;
    private int numeroStanza;
    private double prezzo;
    private RoomType rTipo;
    private BedType lettoTipo;
    private RoomStatus stato;


    // costruttori
    public Room(int id, int id_committente, int numeroStanza, double prezzo, RoomType rTipo, BedType lettoTipo, RoomStatus stato){
        super();

        this.id = id;
        this.id_committente = id_committente;
        this.numeroStanza = numeroStanza;
        this.prezzo = prezzo;
        this.rTipo = rTipo;
        this.lettoTipo = lettoTipo;
        this.stato = stato;
    }

    public Room(int id, double prezzo, RoomType rTipo, BedType lettoTipo, RoomStatus stato){
        super();

        this.id = id;
        this.prezzo = prezzo;
        this.rTipo = rTipo;
        this.lettoTipo = lettoTipo;
        this.stato = stato;
    }
    
    public Room(int id){
        super();
        
        this.id = id;
    }
    
    public Room(){
        
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
     * @return the numeroStanza
     */
    public int getNumeroStanza() {
        return numeroStanza;
    }

    /**
     * @param numeroStanza the numeroStanza to set
     */
    public void setNumeroStanza(int numeroStanza) {
        this.numeroStanza = numeroStanza;
    }

    /**
     * @return the prezzo
     */
    public double getPrezzo() {
        return prezzo;
    }

    /**
     * @param prezzo the prezzo to set
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * @return the rTipo
     */
    public RoomType getrTipo() {
        return rTipo;
    }

    /**
     * @param rTipo the rTipo to set
     */
    public void setrTipo(RoomType rTipo) {
        this.rTipo = rTipo;
    }

    /**
     * @return the lettoTipo
     */
    public BedType getLettoTipo() {
        return lettoTipo;
    }

    /**
     * @param lettoTipo the lettoTipo to set
     */
    public void setLettoTipo(BedType lettoTipo) {
        this.lettoTipo = lettoTipo;
    }

    /**
     * @return the stato
     */
    public RoomStatus getStato() {
        return stato;
    }

    /**
     * @param stato the stato to set
     */
    public void setStato(RoomStatus stato) {
        this.stato = stato;
    }

}
