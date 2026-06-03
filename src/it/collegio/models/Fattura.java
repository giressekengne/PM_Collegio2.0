package it.collegio.models;

import it.collegio.enums.FatturaStatus;
import java.time.LocalDateTime;

public class Fattura {

    private int id;
    private int reservationId;
    private double importo;
    private LocalDateTime dataEmissione;
    private FatturaStatus stato;

    public Fattura() {
    }

    public Fattura(int reservationId, double importo, LocalDateTime dataEmissione, FatturaStatus stato) {
        this.reservationId = reservationId;
        this.importo = importo;
        this.dataEmissione = dataEmissione;
        this.stato = stato;
    }

    public Fattura(int id, int reservationId, double importo, LocalDateTime dataEmissione, FatturaStatus stato) {
        this.id = id;
        this.reservationId = reservationId;
        this.importo = importo;
        this.dataEmissione = dataEmissione;
        this.stato = stato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public LocalDateTime getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(LocalDateTime dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public FatturaStatus getStato() {
        return stato;
    }

    public void setStato(FatturaStatus stato) {
        this.stato = stato;
    }
}
