package it.collegio.controllers;

import it.collegio.dao.IndirizzoDao;
import it.collegio.dao.UserDao;
import it.collegio.enums.Genere;
import it.collegio.enums.UserStatus;
import it.collegio.models.Indirizzo;
import it.collegio.models.User;
import it.collegio.utilities.SessionContext;
import java.util.ArrayList;
import java.util.List;

public class ManageUserController {

    private static final int DEFAULT_COMMITTENTE = 1;
    private static final int DEFAULT_ROLE_CLIENTE = 4;
    private static final int DEFAULT_INDIRIZZO_FALLBACK = 1;

    private final UserDao userDao;
    private final IndirizzoDao indirizzoDao;

    public ManageUserController() {
        this.userDao = new UserDao();
        this.indirizzoDao = new IndirizzoDao();
    }

    public List<User> getUsersPerRuolo() {
        if ("U".equalsIgnoreCase(SessionContext.roleType)) {
            User self = userDao.getByCounter(SessionContext.userCounter);
            List<User> only = new ArrayList<>();
            if (self != null) only.add(self);
            return only;
        }
        return userDao.getAll();
    }

    public User cercaUserPerEmail(String email) {
        return userDao.getUser(email);
    }

    public String generateUserCounter(String nome) {
        return userDao.generateUniqueCounter(nome);
    }

    public boolean insertUser(String nome, String cognome, String email, String password,
                              String stato, String mobile, String via,
                              String recupero, String response, String genere) {

        if (nome == null || nome.isEmpty() || email == null || email.isEmpty()
                || password == null || password.isEmpty()) {
            return false;
        }

        if (userDao.existsByEmail(email)) {
            return false;
        }

        String counter = generateUserCounter(nome);
        if (counter == null) {
            return false;
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
        user.setStato(parseUserStatus(stato));
        user.setMobile(mobile);
        user.setAddress(indirizzoId);
        user.setRecupero(recupero);
        user.setResponse(response);
        user.setGenere(parseGenere(genere));

        return userDao.insertUser(user);
    }

    public boolean updateUser(String email, String nome, String cognome, String password,
                              String mobile, String stato, String recupero,
                              String response, String genere) {

        User existing = userDao.getUser(email);
        if (existing == null) {
            return false;
        }

        existing.setNome(nome);
        existing.setCognome(cognome);
        existing.setPw(password);
        existing.setMobile(mobile);
        existing.setStato(parseUserStatus(stato));
        existing.setRecupero(recupero);
        existing.setResponse(response);
        existing.setGenere(parseGenere(genere));

        return userDao.UpdateUser(existing);
    }

    public boolean deleteUser(String userCounter) {
        return userDao.deleteUser(userCounter);
    }

    private UserStatus parseUserStatus(String value) {
        if (value == null) return UserStatus.ATTESA;
        try {
            return UserStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return UserStatus.ATTESA;
        }
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
