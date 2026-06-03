package it.collegio.controllers;

import it.collegio.dao.CommittenteDao;
import it.collegio.dao.IndirizzoDao;
import it.collegio.dao.UserDao;
import it.collegio.dto.CommittenteDettaglio;
import it.collegio.models.Committente;
import it.collegio.models.Indirizzo;
import it.collegio.models.User;
import java.util.List;

public class ManageTenantController {

    private static final int ROLE_CLIENTE = 4;
    private static final int DEFAULT_INDIRIZZO_FALLBACK = 1;

    private final CommittenteDao committenteDao;
    private final UserDao userDao;
    private final IndirizzoDao indirizzoDao;

    public ManageTenantController() {
        this.committenteDao = new CommittenteDao();
        this.userDao = new UserDao();
        this.indirizzoDao = new IndirizzoDao();
    }

    public List<CommittenteDettaglio> getDettagli() {
        return committenteDao.getDettagli();
    }

    public CommittenteDettaglio getDettaglio(int id) {
        return committenteDao.getDettaglio(id);
    }

    public int getNextId() {
        return committenteDao.getMaxId() + 1;
    }

    public List<String> getAdminEmails() {
        return userDao.getEmailsByRoleNotIn(ROLE_CLIENTE);
    }

    public List<Indirizzo> getIndirizzi() {
        return indirizzoDao.getAll();
    }

    public boolean addCommittente(int codCommittente, String ragioneSociale,
                                   String gestoreEmail, String email,
                                   String telefono, String via) {
        if (ragioneSociale == null || ragioneSociale.isEmpty() || email == null || email.isEmpty()) {
            return false;
        }

        Committente c = buildCommittente(codCommittente, ragioneSociale, gestoreEmail, email, telefono, via);
        if (c == null) return false;
        return committenteDao.insertCommittenteWithId(c);
    }

    public boolean updateCommittente(int codCommittente, String ragioneSociale,
                                      String gestoreEmail, String email,
                                      String telefono, String via) {
        Committente c = buildCommittente(codCommittente, ragioneSociale, gestoreEmail, email, telefono, via);
        if (c == null) return false;
        return committenteDao.updateCommittente(c);
    }

    private Committente buildCommittente(int codCommittente, String ragioneSociale,
                                          String gestoreEmail, String email,
                                          String telefono, String via) {
        Committente c = new Committente();
        c.setId(codCommittente);
        c.setNome(ragioneSociale);
        c.setEmail(email);
        c.setMobile(telefono);

        int indirizzoId = DEFAULT_INDIRIZZO_FALLBACK;
        Indirizzo i = indirizzoDao.getByVia(via);
        if (i != null) {
            indirizzoId = i.getId();
        }
        c.setAddress(indirizzoId);

        if (gestoreEmail != null && !gestoreEmail.isEmpty()) {
            User gestore = userDao.getUser(gestoreEmail);
            if (gestore != null) {
                c.setAdmin(gestore);
            }
        }
        return c;
    }
}
