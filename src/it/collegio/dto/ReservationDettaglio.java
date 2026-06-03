package it.collegio.dto;

public class ReservationDettaglio {

    private int reservationId;
    private String userNome;
    private int committenteId;
    private int roomId;
    private double prezzoGiornaliero;
    private String checkIn;
    private String checkOut;
    private String stato;
    private String note;
    private int giorni;
    private double totale;

    public ReservationDettaglio() {
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getUserNome() { return userNome; }
    public void setUserNome(String userNome) { this.userNome = userNome; }

    public int getCommittenteId() { return committenteId; }
    public void setCommittenteId(int committenteId) { this.committenteId = committenteId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public double getPrezzoGiornaliero() { return prezzoGiornaliero; }
    public void setPrezzoGiornaliero(double prezzoGiornaliero) { this.prezzoGiornaliero = prezzoGiornaliero; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getGiorni() { return giorni; }
    public void setGiorni(int giorni) { this.giorni = giorni; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }
}
