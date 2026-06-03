package it.collegio.controllers;

import it.collegio.dao.IndirizzoDao;
import it.collegio.dao.UserDao;
import it.collegio.dto.LoginResponse;
import it.collegio.enums.Genere;
import it.collegio.enums.UserStatus;
import it.collegio.models.Indirizzo;
import it.collegio.models.User;
import java.util.List;

public class RegistrationController {

    private static final int DEFAULT_COMMITTENTE = 1;
    private static final int DEFAULT_ROLE_CLIENTE = 4;
    private static final int DEFAULT_INDIRIZZO_FALLBACK = 1;

    private final UserDao userDao;
    private final IndirizzoDao indirizzoDao;

    public RegistrationController() {
        this.userDao = new UserDao();
        this.indirizzoDao = new IndirizzoDao();
    }

    /** Indirizzi disponibili per il combo "via" nel form di registrazione. */
    public List<Indirizzo> getIndirizzi() {
        return indirizzoDao.getAll();
    }

    /**
     * Registra un nuovo utente come cliente (ruolo U) in stato 'attesa'.
     * Genera lo user_counter univoco. Ritorna LoginResponse con esito + utente
     * inserito (per coerenza con login) oppure errore.
     */
    public LoginResponse registra(String nome, String cognome, String email,
                                   String password, String telefono,
                                   String via, String domanda, String risposta,
                                   String genere) {

        if (nome == null || nome.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()) {
            return LoginResponse.error("Nome, Email e Password sono obbligatori");
        }

        if (userDao.existsByEmail(email)) {
            return LoginResponse.error("Email gia in uso");
        }

        String counter = userDao.generateUniqueCounter(nome);
        if (counter == null) {
            return LoginResponse.error("Impossibile generare lo user counter");
        }

        int indirizzoId = DEFAULT_INDIRIZZO_FALLBACK;
        Indirizzo indirizzo = indirizzoDao.getByVia(via);
        if (indirizzo != null) {
            indirizzoId = indirizzo.getId();
        }

        User user = new User();
        user.setCounter(counter);
        user.setNome(nome);
        user.setCognome(cognome);
        user.setEmail(email.toLowerCase());
        user.setPw(password);
        user.setRole(DEFAULT_ROLE_CLIENTE);
        user.setCommittente(DEFAULT_COMMITTENTE);
        user.setStato(UserStatus.ATTESA);
        user.setMobile(telefono);
        user.setAddress(indirizzoId);
        user.setRecupero(domanda);
        user.setResponse(risposta);
        user.setGenere(parseGenere(genere));

        if (!userDao.insertUser(user)) {
            return LoginResponse.error("Errore inserimento utente");
        }
        return LoginResponse.ok(user);
    }

    private Genere parseGenere(String value) {
        if (value == null) return Genere.ALTRO;
        try {
            return Genere.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Genere.ALTRO;
        }
    }
}
