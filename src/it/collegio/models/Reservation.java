package it.collegio.models;

import it.collegio.enums.ReservationStatus;
import java.util.Date;


public class Reservation {
    
    private String id;
    private String user;
    private int id_committente;
    private int id_room;
    private Date checkIn;
    private Date checkOut;
    private ReservationStatus stato;
    private String note;
    private int giorni;
    private double total;
    
    //Costruttori
    public Reservation(String id, String user,int id_committente, int id_room,
            Date checkIn, Date checkOut, ReservationStatus stato, String note, int giorni, double total){
        
        super();
        
        this.id = id;
        this.user = user;
        this.id_committente = id_committente;
        this.id_room = id_room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.stato = stato;
        this.note = note;
        this.giorni = giorni;
        this.total = total;
    }
    
    public Reservation(String id, String user, int id_committente, int id_room,
            Date checkIn, ReservationStatus stato, String note){
        
        super();
        
        this.id = id;
        this.user = user;
        this.id_committente = id_committente;
        this.id_room = id_room;
        this.checkIn = checkIn;
        this.stato = stato;
        this.note = note;
        
    }
    
    public Reservation(String id, Date checkOut, ReservationStatus stato, String note, int giorni, double total){
        
        super();
        
        this.id = id;
        this.checkOut = checkOut;
        this.stato = stato;
        this.note = note;
        this.giorni = giorni;
        this.total = total;
        
    }
    
    public Reservation(String id){
        
        super();
        
        this.id = id;
             
    }
    
    public Reservation(){
        
             
    }
    
    

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
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
     * @return the room
     */
    public int getRoom() {
        return id_room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(int id_room) {
        this.id_room = id_room;
    }

    /**
     * @return the checkIn
     */
    public Date getCheckIn() {
        return checkIn;
    }

    /**
     * @param checkIn the checkIn to set
     */
    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    /**
     * @return the checkOut
     */
    public Date getCheckOut() {
        return checkOut;
    }

    /**
     * @param checkOut the checkOut to set
     */
    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    /**
     * @return the stato
     */
    public ReservationStatus getStato() {
        return stato;
    }

    /**
     * @param stato the stato to set
     */
    public void setStato(ReservationStatus stato) {
        this.stato = stato;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the giorni
     */
    public int getGiorni() {
        return giorni;
    }

    /**
     * @param giorni the giorni to set
     */
    public void setGiorni(int giorni) {
        this.giorni = giorni;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }
    
    
}
