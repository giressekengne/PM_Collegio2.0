package it.collegio.models;

import java.time.LocalDateTime;

public class Pagamento {

    private int id;
    private int fatturaId;
    private int metodoId;
    private double importo;
    private LocalDateTime dataPagamento;

    public Pagamento() {
    }

    public Pagamento(int fatturaId, int metodoId, double importo) {
        this.fatturaId = fatturaId;
        this.metodoId = metodoId;
        this.importo = importo;
    }

    public Pagamento(int id, int fatturaId, int metodoId, double importo, LocalDateTime dataPagamento) {
        this.id = id;
        this.fatturaId = fatturaId;
        this.metodoId = metodoId;
        this.importo = importo;
        this.dataPagamento = dataPagamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFatturaId() {
        return fatturaId;
    }

    public void setFatturaId(int fatturaId) {
        this.fatturaId = fatturaId;
    }

    public int getMetodoId() {
        return metodoId;
    }

    public void setMetodoId(int metodoId) {
        this.metodoId = metodoId;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}
