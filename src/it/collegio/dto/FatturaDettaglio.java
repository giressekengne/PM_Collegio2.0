package it.collegio.dto;

import it.collegio.enums.FatturaStatus;
import java.time.LocalDateTime;

public class FatturaDettaglio {

    private int fatturaId;
    private int reservationId;
    private String clienteNome;
    private int numeroStanza;
    private double importo;
    private LocalDateTime dataEmissione;
    private FatturaStatus stato;
    private String reservationStato;

    public FatturaDettaglio() {
    }

    public int getFatturaId() {
        return fatturaId;
    }

    public void setFatturaId(int fatturaId) {
        this.fatturaId = fatturaId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public int getNumeroStanza() {
        return numeroStanza;
    }

    public void setNumeroStanza(int numeroStanza) {
        this.numeroStanza = numeroStanza;
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

    public String getReservationStato() {
        return reservationStato;
    }

    public void setReservationStato(String reservationStato) {
        this.reservationStato = reservationStato;
    }
}
