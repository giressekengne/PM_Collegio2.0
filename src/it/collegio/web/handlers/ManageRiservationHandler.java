package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.ReservationController;
import it.collegio.dto.ReservationDettaglio;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET  /reservation         -> lista filtrata per ruolo (Controller fa il filtro)
 * POST /reservation/update  -> aggiornamento con storico (transazione)
 *
 * Per il ruolo U: configureForRole disabilita Update lato Controller (la lista e' filtrata
 * solo sulle proprie prenotazioni). In v2 il filtro e' una sola riga di codice nel Controller,
 * non duplicato tra Swing e HTML.
 */
public class ManageRiservationHandler implements HttpHandler {

    private final ReservationController controller = new ReservationController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String alert = "";
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            // ruolo U: read-only
            if ("U".equalsIgnoreCase(SessionContext.roleType)) {
                alert = "<div class=\"alert alert-warning\">Il tuo ruolo non puo' modificare le prenotazioni</div>";
            } else {
                Map<String, String> form = HandlerUtils.parseForm(ex);
                int id;
                try {
                    id = Integer.parseInt(form.getOrDefault("reservationId", "0"));
                } catch (NumberFormatException nfe) {
                    id = 0;
                }
                boolean ok = controller.aggiornaConStorico(
                        id,
                        form.getOrDefault("checkIn", "").trim(),
                        form.getOrDefault("checkOut", "").trim(),
                        form.getOrDefault("note", "").trim()
                );
                alert = ok
                        ? "<div class=\"alert alert-success\">Prenotazione #" + id + " aggiornata.</div>"
                        : "<div class=\"alert alert-danger\">Impossibile aggiornare la prenotazione #" + id + "</div>";
            }
        }

        List<ReservationDettaglio> list = controller.getDettagliPerRuolo();
        boolean isUser = "U".equalsIgnoreCase(SessionContext.roleType);

        StringBuilder rows = new StringBuilder();
        if (list == null || list.isEmpty()) {
            rows.append("<tr><td colspan=\"9\" class=\"text-center text-muted\">Nessuna prenotazione</td></tr>");
        } else {
            for (ReservationDettaglio r : list) {
                rows.append("<tr>")
                    .append("<td><code>P").append(String.format("%04d", r.getReservationId())).append("</code></td>")
                    .append("<td>").append(HtmlRenderer.escape(r.getUserNome())).append("</td>")
                    .append("<td>R").append(String.format("%04d", r.getRoomId())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(r.getCheckIn())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(r.getCheckOut())).append("</td>")
                    .append("<td>").append(r.getGiorni()).append("</td>")
                    .append("<td>").append(String.format("%.2f", r.getTotale())).append(" &euro;</td>")
                    .append("<td>").append(statoBadge(r.getStato())).append("</td>")
                    .append("<td>");
                if (!isUser) {
                    rows.append("<button class=\"btn btn-sm btn-outline-pmc\" data-bs-toggle=\"collapse\" "
                              + "data-bs-target=\"#edit-").append(r.getReservationId()).append("\">Modifica</button>");
                } else {
                    rows.append("<span class=\"text-muted small\">read-only</span>");
                }
                rows.append("</td></tr>");

                if (!isUser) {
                    rows.append("<tr class=\"collapse\" id=\"edit-").append(r.getReservationId()).append("\">")
                        .append("<td colspan=\"9\" class=\"bg-light\">")
                        .append("<form method=\"post\" action=\"/reservation\" class=\"row g-2 align-items-end\">")
                        .append("<input type=\"hidden\" name=\"reservationId\" value=\"").append(r.getReservationId()).append("\">")
                        .append("<div class=\"col-md-3\"><label class=\"form-label small mb-1\">Nuovo check-in</label>")
                        .append("<input type=\"date\" name=\"checkIn\" class=\"form-control form-control-sm\" value=\"").append(HtmlRenderer.escape(r.getCheckIn())).append("\" required></div>")
                        .append("<div class=\"col-md-3\"><label class=\"form-label small mb-1\">Nuovo check-out</label>")
                        .append("<input type=\"date\" name=\"checkOut\" class=\"form-control form-control-sm\" value=\"").append(HtmlRenderer.escape(r.getCheckOut())).append("\"></div>")
                        .append("<div class=\"col-md-4\"><label class=\"form-label small mb-1\">Note</label>")
                        .append("<input type=\"text\" name=\"note\" class=\"form-control form-control-sm\" value=\"").append(HtmlRenderer.escape(r.getNote())).append("\"></div>")
                        .append("<div class=\"col-md-2\"><button type=\"submit\" class=\"btn btn-pmc btn-sm w-100\">Salva</button></div>")
                        .append("</form>")
                        .append("</td></tr>");
                }
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(list != null ? list.size() : 0));
        values.put("alert", alert);
        values.put("filterNote", isUser
                ? "Vedi solo le tue prenotazioni"
                : "Vista completa");

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("manage_reservation.html", "Prenotazioni - PM Collegio", values, layout));
    }

    private String statoBadge(String stato) {
        if (stato == null) return "<span class=\"badge bg-secondary\">?</span>";
        switch (stato.toLowerCase()) {
            case "attiva":     return "<span class=\"badge bg-success\">Attiva</span>";
            case "completata": return "<span class=\"badge bg-primary\">Completata</span>";
            case "cancellata": return "<span class=\"badge bg-danger\">Cancellata</span>";
            default:           return "<span class=\"badge bg-secondary\">" + HtmlRenderer.escape(stato) + "</span>";
        }
    }
}
