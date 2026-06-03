package it.collegio.dto;

public class CommittenteDettaglio {

    private int codCommittente;
    private String ragioneSociale;
    private String gestoreEmail;
    private String email;
    private String telefono;
    private String via;

    public CommittenteDettaglio() {
    }

    public int getCodCommittente() { return codCommittente; }
    public void setCodCommittente(int codCommittente) { this.codCommittente = codCommittente; }

    public String getRagioneSociale() { return ragioneSociale; }
    public void setRagioneSociale(String ragioneSociale) { this.ragioneSociale = ragioneSociale; }

    public String getGestoreEmail() { return gestoreEmail; }
    public void setGestoreEmail(String gestoreEmail) { this.gestoreEmail = gestoreEmail; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }
}
