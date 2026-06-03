package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.CheckInController;
import it.collegio.models.Room;
import it.collegio.models.User;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET  /checkin                          -> form vuoto (camere = tutte disponibili)
 * GET  /checkin?searchEmail=X            -> form pre-popolato con dati utente (cercaUserPerEmail)
 * GET  /checkin?prezzo=X                 -> filtra le camere per prezzo (getRoomPerPrezzo)
 * POST /checkin action=search            -> ridireziona a GET con searchEmail (PRG pattern)
 * POST /checkin action=book              -> CheckInController.prenota() in transazione
 *
 * Stessa logica della CheckInView Swing: la ricerca utente popola nome/cognome/genere/mobile
 * dal DB, e l'utente puo' filtrare le camere disponibili per prezzo.
 */
public class CheckInHandler implements HttpHandler {

    private final CheckInController controller = new CheckInController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String alert = "";
        Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
        String searchEmail = q.get("searchEmail");
        String prezzoFiltro = q.get("prezzo");

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> form = HandlerUtils.parseForm(ex);
            String action = form.getOrDefault("action", "book");

            if ("search".equals(action)) {
                // Pattern Post-Redirect-Get: redirect a GET con i parametri di search
                String email = form.getOrDefault("email", "").trim();
                String prezzo = form.getOrDefault("prezzoFiltro", "").trim();
                StringBuilder url = new StringBuilder("/checkin");
                boolean hasParam = false;
                if (!email.isEmpty()) {
                    url.append("?searchEmail=").append(urlEncode(email));
                    hasParam = true;
                }
                if (!prezzo.isEmpty()) {
                    url.append(hasParam ? "&" : "?").append("prezzo=").append(urlEncode(prezzo));
                }
                HandlerUtils.redirect(ex, url.toString());
                return;
            }

            // action = book
            String email = form.getOrDefault("email", "").trim();
            User user = controller.cercaUserPerEmail(email);
            if (user == null) {
                alert = "<div class=\"alert alert-danger\">Utente con email " + HtmlRenderer.escape(email) + " non trovato</div>";
                searchEmail = email;
            } else {
                int roomId = parseInt(form.getOrDefault("roomId", "0"));
                int committenteId = user.getCommittente() > 0 ? user.getCommittente() : 1;
                int reservationId = controller.prenota(
                        user.getCounter(),
                        roomId,
                        committenteId,
                        form.getOrDefault("checkInDate", "").trim(),
                        form.getOrDefault("note", "").trim()
                );
                if (reservationId > 0) {
                    alert = "<div class=\"alert alert-success\">Check-in eseguito. Prenotazione "
                          + "<strong>P" + String.format("%04d", reservationId) + "</strong> creata.</div>";
                    searchEmail = null; // pulisci dopo successo
                } else {
                    alert = "<div class=\"alert alert-danger\">Check-in fallito</div>";
                    searchEmail = email;
                }
            }
        }

        // Lookup utente per pre-popolamento
        User foundUser = null;
        String userAlert = "";
        if (searchEmail != null && !searchEmail.isEmpty()) {
            foundUser = controller.cercaUserPerEmail(searchEmail);
            if (foundUser == null && alert.isEmpty()) {
                userAlert = "<div class=\"alert alert-warning\">Nessun utente con email "
                          + HtmlRenderer.escape(searchEmail) + ". Devi prima registrarlo da /registration.</div>";
            }
        }

        // Filtro camere per prezzo (se richiesto)
        List<Room> rooms;
        double prezzoSelezionato = -1;
        if (prezzoFiltro != null && !prezzoFiltro.isEmpty()) {
            try {
                prezzoSelezionato = Double.parseDouble(prezzoFiltro.replace(",", "."));
                rooms = controller.getRoomPerPrezzo(prezzoSelezionato);
            } catch (NumberFormatException nfe) {
                rooms = controller.getRoomDisponibili();
            }
        } else {
            rooms = controller.getRoomDisponibili();
        }

        StringBuilder roomOptions = new StringBuilder();
        roomOptions.append("<option value=\"\">-- seleziona --</option>");
        if (rooms != null) {
            for (Room r : rooms) {
                roomOptions.append("<option value=\"").append(r.getId()).append("\">")
                           .append("R").append(String.format("%04d", r.getId()))
                           .append(" - ").append(r.getrTipo())
                           .append(" - ").append(String.format("%.2f", r.getPrezzo())).append(" €")
                           .append("</option>");
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("alert", alert.isEmpty() ? userAlert : alert);
        values.put("roomOptions", roomOptions.toString());
        values.put("availableCount", String.valueOf(rooms != null ? rooms.size() : 0));
        values.put("searchEmail", searchEmail != null ? HtmlRenderer.escape(searchEmail) : "");
        values.put("prezzoFiltro", prezzoFiltro != null ? HtmlRenderer.escape(prezzoFiltro) : "");
        values.put("userBlock", renderUserBlock(foundUser));
        values.put("formDisabled", foundUser == null ? "disabled" : "");

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("checkin.html", "Check-in - PM Collegio", values, layout));
    }

    private String renderUserBlock(User u) {
        if (u == null) {
            return "<div class=\"alert alert-info small mb-0\">Inserisci l'email di un cliente "
                 + "gia' registrato e clicca <strong>Cerca</strong>. Il check-in si abilita "
                 + "solo dopo aver trovato l'utente.</div>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"alert alert-success mb-0\">")
          .append("<strong>Utente trovato:</strong> ")
          .append(HtmlRenderer.escape(u.getNome())).append(" ").append(HtmlRenderer.escape(u.getCognome()))
          .append(" &middot; <code>").append(HtmlRenderer.escape(u.getCounter())).append("</code>")
          .append(" &middot; ").append(HtmlRenderer.escape(u.getMobile() != null ? u.getMobile() : ""))
          .append("</div>");
        return sb.toString();
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> m = new HashMap<>();
        if (query == null) return m;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    m.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()));
                } catch (Exception ignore) {
                    m.put(kv[0], kv[1]);
                }
            }
        }
        return m;
    }

    private String urlEncode(String s) {
        try { return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.name()); }
        catch (Exception e) { return s; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
