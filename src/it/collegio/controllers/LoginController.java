package it.collegio.controllers;

import it.collegio.dao.AccessLogDao;
import it.collegio.dao.MansioneDao;
import it.collegio.dao.SessionsDao;
import it.collegio.dao.UserDao;
import it.collegio.dto.LoginResponse;
import it.collegio.enums.UserStatus;
import it.collegio.models.AccessLog;
import it.collegio.models.Mansione;
import it.collegio.models.Sessions;
import it.collegio.models.User;
import it.collegio.utilities.SessionContext;
import java.time.LocalDateTime;

public class LoginController {

    private final UserDao userDao;
    private final MansioneDao mansioneDao;
    private final AccessLogDao accessLogDao;
    private final SessionsDao sessionsDao;

    public LoginController() {
        this.userDao      = new UserDao();
        this.mansioneDao  = new MansioneDao();
        this.accessLogDao = new AccessLogDao();
        this.sessionsDao  = new SessionsDao();
    }

    public LoginResponse login(String email, String password) {

        User credentials = new User(email, password);
        if (!userDao.ValidateUser(credentials)) {
            return LoginResponse.error("Credenziali errate");
        }

        User user = userDao.getUser(email);
        if (user == null) {
            return LoginResponse.error("Utente non trovato");
        }

        if (user.getStato() != UserStatus.ATTIVO) {
            return LoginResponse.error("Account non attivo o in attesa di approvazione");
        }

        Mansione mansione = mansioneDao.getById(user.getRole());

        AccessLog log = new AccessLog(user.getCounter(), "127.0.0.1", "Desktop App");
        int logId = accessLogDao.insertLog(log);

        String token = SessionsDao.generaToken();
        LocalDateTime expires = LocalDateTime.now().plusHours(SessionsDao.DURATA_ORE);

        Sessions session = new Sessions();
        session.setUser(user.getCounter());
        session.setCommittente(user.getCommittente() > 0 ? user.getCommittente() : 1);
        session.setRole(user.getRole());
        session.setToken(token);
        session.setExpired(expires);

        int sessionId = sessionsDao.insertSession(session);

        SessionContext.userCounter   = user.getCounter();
        SessionContext.email         = user.getEmail();
        SessionContext.nome          = user.getNome();
        SessionContext.cognome       = user.getCognome();
        SessionContext.logId         = logId;
        SessionContext.sessionId     = sessionId;
        SessionContext.committenteId = user.getCommittente();
        SessionContext.roleId        = user.getRole();
        SessionContext.sessionToken  = token;
        if (mansione != null) {
            SessionContext.roleNome = mansione.getNome();
            SessionContext.roleType = mansione.getTipo();
        }

        return LoginResponse.ok(user);
    }

    public boolean registraLogout() {
        if (!SessionContext.isLoggedIn()) {
            return false;
        }

        boolean logUpdated = true;
        if (SessionContext.logId > 0) {
            logUpdated = accessLogDao.updateLogout(SessionContext.logId, "Logout normale");
        }

        boolean sessionInvalidated = true;
        if (SessionContext.sessionId > 0) {
            sessionInvalidated = sessionsDao.invalidateSession(SessionContext.sessionId);
        }

        SessionContext.clear();
        return logUpdated && sessionInvalidated;
    }
}
