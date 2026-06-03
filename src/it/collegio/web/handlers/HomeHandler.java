package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Replica esatta della logica di HomeView.configureButtonsForRole(): le tile della home
 * sono mostrate o nascoste in base al ruolo letto da SessionContext (popolato dal
 * LoginController). Stessa logica di visibilita' della versione Swing.
 */
public class HomeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String rt = SessionContext.roleType != null ? SessionContext.roleType : "";
        boolean isAS = "AS".equalsIgnoreCase(rt);
        boolean isAC = "AC".equalsIgnoreCase(rt);

        StringBuilder tiles = new StringBuilder();
        tiles.append(tile("/rooms",             "Manage Room",        "Gestisci le camere del collegio.",        isAS || isAC));
        tiles.append(tile("/reservation",       "Manage Reservation", "Prenotazioni e storico modifiche.",      true));
        tiles.append(tile("/users",             "Manage User",        "Utenti del sistema.",                     true));
        tiles.append(tile("/tenants",           "Manage Tenant",      "Committenti registrati.",                 isAS));
        tiles.append(tile("/checkin",           "Check-in",           "Apri una nuova prenotazione.",            true));
        tiles.append(tile("/checkout",          "Check-out",          "Chiudi prenotazione e genera fattura.",   true));
        tiles.append(tile("/gestione-fatture",  "Gestione fatture",   "Lista fatture, pagamenti, annullamenti.", true));
        tiles.append(tile("/logs",              "Access Logs",        "Audit degli accessi al sistema.",         isAS || isAC));

        String displayName = (SessionContext.nome != null ? SessionContext.nome : "")
                + " "
                + (SessionContext.cognome != null ? SessionContext.cognome : "");

        Map<String, String> values = new HashMap<>();
        values.put("userName", HtmlRenderer.escape(displayName.trim()));
        values.put("roleType", HtmlRenderer.escape(rt));
        values.put("tiles",    tiles.toString());

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape(displayName.trim()) + " (" + HtmlRenderer.escape(rt) + ")");
        String html = HtmlRenderer.renderInLayout("home.html", "Home - PM Collegio", values, layout);
        HandlerUtils.sendHtml(ex, html);
    }

    private String tile(String href, String title, String desc, boolean visible) {
        if (!visible) return "";
        return "<a class=\"tile\" href=\"" + href + "\">"
             + "<h3>" + HtmlRenderer.escape(title) + "</h3>"
             + "<p>" + HtmlRenderer.escape(desc) + "</p>"
             + "</a>";
    }
}
