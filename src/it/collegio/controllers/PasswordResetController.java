package it.collegio.controllers;

import it.collegio.dao.UserDao;
import it.collegio.models.User;

public class PasswordResetController {

    private final UserDao userDao;

    public PasswordResetController() {
        this.userDao = new UserDao();
    }

    /**
     * Restituisce la domanda di sicurezza ('recupero') per l'email,
     * oppure null se l'email non esiste.
     */
    public String getDomandaSicurezza(String email) {
        if (email == null || email.isEmpty()) return null;
        User user = userDao.getUser(email);
        return user != null ? user.getRecupero() : null;
    }

    /**
     * Verifica che la risposta combaci con quella registrata e aggiorna
     * la password. Ritorna true solo se entrambe le operazioni vanno a buon
     * fine.
     */
    public boolean resetPassword(String email, String risposta, String nuovaPassword) {
        if (email == null || email.isEmpty()
                || risposta == null || risposta.isEmpty()
                || nuovaPassword == null || nuovaPassword.isEmpty()) {
            return false;
        }

        User user = userDao.getUser(email);
        if (user == null) {
            return false;
        }

        if (user.getResponse() == null || !user.getResponse().equals(risposta)) {
            return false;
        }

        return userDao.updatePasswordByEmail(email, nuovaPassword);
    }
}
