package it.collegio.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import it.collegio.utilities.SessionContext;

import java.util.List;
import java.util.UUID;

/**
 * Gestione della sessione lato browser tramite cookie.
 *
 * NOTA IMPORTANTE: la v2 desktop usa SessionContext con campi static. Sotto Swing
 * va bene perche' c'e' un solo utente. Sul server HTTP, piu' richieste possono
 * arrivare in parallelo: per la demo monoutente da localhost lo lasciamo cosi',
 * ma in un sistema reale qui ci vorrebbe una mappa token -> SessionData per-utente
 * e si rimpiazzerebbero gli accessi statici a SessionContext con chiamate alla mappa.
 */
public final class WebSession {

    private static final String COOKIE_NAME = "PM_SID";

    private WebSession() {
    }

    public static String readCookie(HttpExchange ex) {
        List<String> cookies = ex.getRequestHeaders().get("Cookie");
        if (cookies == null) return null;
        for (String header : cookies) {
            for (String part : header.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && COOKIE_NAME.equals(kv[0])) {
                    return kv[1];
                }
            }
        }
        return null;
    }

    public static String issueCookie(HttpExchange ex) {
        String sid = UUID.randomUUID().toString().replace("-", "");
        Headers h = ex.getResponseHeaders();
        h.add("Set-Cookie", COOKIE_NAME + "=" + sid + "; Path=/; HttpOnly; SameSite=Lax");
        return sid;
    }

    public static void clearCookie(HttpExchange ex) {
        ex.getResponseHeaders().add("Set-Cookie",
                COOKIE_NAME + "=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
    }

    public static boolean isLoggedIn() {
        return SessionContext.isLoggedIn();
    }
}
