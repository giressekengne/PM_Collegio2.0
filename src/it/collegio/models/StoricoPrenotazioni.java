package it.collegio.models;

import java.util.Date;

public class StoricoPrenotazioni {

    private int id;
    private int reservationId;
    private String userId;
    private Date checkInPrecedente;
    private Date checkOutPrecedente;
    private Date nuovoCheckIn;
    private Date nuovoCheckOut;
    private Date dataModifica;

    public StoricoPrenotazioni() {
    }

    public StoricoPrenotazioni(int reservationId, String userId,
                               Date checkInPrecedente, Date checkOutPrecedente,
                               Date nuovoCheckIn, Date nuovoCheckOut) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.checkInPrecedente = checkInPrecedente;
        this.checkOutPrecedente = checkOutPrecedente;
        this.nuovoCheckIn = nuovoCheckIn;
        this.nuovoCheckOut = nuovoCheckOut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Date getCheckInPrecedente() { return checkInPrecedente; }
    public void setCheckInPrecedente(Date checkInPrecedente) { this.checkInPrecedente = checkInPrecedente; }

    public Date getCheckOutPrecedente() { return checkOutPrecedente; }
    public void setCheckOutPrecedente(Date checkOutPrecedente) { this.checkOutPrecedente = checkOutPrecedente; }

    public Date getNuovoCheckIn() { return nuovoCheckIn; }
    public void setNuovoCheckIn(Date nuovoCheckIn) { this.nuovoCheckIn = nuovoCheckIn; }

    public Date getNuovoCheckOut() { return nuovoCheckOut; }
    public void setNuovoCheckOut(Date nuovoCheckOut) { this.nuovoCheckOut = nuovoCheckOut; }

    public Date getDataModifica() { return dataModifica; }
    public void setDataModifica(Date dataModifica) { this.dataModifica = dataModifica; }
}
