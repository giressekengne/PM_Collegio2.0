package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.CheckOutController;
import it.collegio.dto.CheckoutPreview;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GET  /checkout                  -> form base (id prenotazione + data)
 * POST /checkout (action=preview) -> mostra il preview (giorni, totale) calcolato dal Controller
 * POST /checkout (action=confirm) -> esegue la transazione di checkout
 *
 * Il calcolo del totale e dei giorni e' sempre server-side, mai dalla view: questo
 * vale tanto per la CheckOutView Swing quanto per l'HTML.
 */
public class CheckOutHandler implements HttpHandler {

    private final CheckOutController controller = new CheckOutController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        Map<String, String> form = new HashMap<>();
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            form = HandlerUtils.parseForm(ex);
        }

        String action = form.getOrDefault("action", "");
        int reservationId = parseInt(form.getOrDefault("reservationId", "0"));
        String checkOutDate = form.getOrDefault("checkOutDate", "").trim();

        String alert = "";
        String previewBlock = "";

        if ("preview".equals(action)) {
            CheckoutPreview p = controller.getCheckoutPreview(reservationId, checkOutDate);
            if (p == null) {
                alert = "<div class=\"alert alert-danger\">Prenotazione " + reservationId + " non trovata o dati incoerenti</div>";
            } else {
                previewBlock = renderPreview(p);
            }
        } else if ("confirm".equals(action)) {
            int fatturaId = controller.eseguiCheckout(reservationId, checkOutDate);
            if (fatturaId > 0) {
                alert = "<div class=\"alert alert-success\">Check-out eseguito. Fattura "
                      + "<strong>F" + String.format("%04d", fatturaId) + "</strong> emessa. "
                      + "<a href=\"/fattura?id=" + fatturaId + "\" class=\"alert-link\">Vedi dettaglio</a></div>";
            } else {
                alert = "<div class=\"alert alert-danger\">Check-out fallito</div>";
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("alert", alert);
        values.put("preview", previewBlock);
        values.put("reservationId", reservationId > 0 ? String.valueOf(reservationId) : "");
        values.put("checkOutDate", HtmlRenderer.escape(checkOutDate));

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("checkout.html", "Check-out - PM Collegio", values, layout));
    }

    private String renderPreview(CheckoutPreview p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card border-0 shadow-sm mb-4\"><div class=\"card-body p-4\">")
          .append("<h2 class=\"h5 mb-3\">Preview check-out</h2>")
          .append("<dl class=\"row mb-3\">")
          .append("<dt class=\"col-sm-4\">Prenotazione</dt><dd class=\"col-sm-8\">P").append(String.format("%04d", p.getReservationId())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Cliente</dt><dd class=\"col-sm-8\">").append(HtmlRenderer.escape(p.getUserNome())).append(" - ").append(HtmlRenderer.escape(p.getUserEmail())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Camera</dt><dd class=\"col-sm-8\">R").append(String.format("%04d", p.getRoomId())).append(" (").append(String.format("%.2f", p.getPrezzoGiornaliero())).append(" €/notte)</dd>")
          .append("<dt class=\"col-sm-4\">Check-in</dt><dd class=\"col-sm-8\">").append(HtmlRenderer.escape(p.getCheckInDate())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Check-out</dt><dd class=\"col-sm-8\">").append(HtmlRenderer.escape(p.getCheckOutDate())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Giorni</dt><dd class=\"col-sm-8\">").append(p.getGiorni()).append("</dd>")
          .append("<dt class=\"col-sm-4\">Totale</dt><dd class=\"col-sm-8\"><strong class=\"fs-4 text-pmc\">").append(String.format("%.2f", p.getTotale())).append(" €</strong></dd>")
          .append("</dl>")
          .append("<form method=\"post\" action=\"/checkout\">")
          .append("<input type=\"hidden\" name=\"action\" value=\"confirm\">")
          .append("<input type=\"hidden\" name=\"reservationId\" value=\"").append(p.getReservationId()).append("\">")
          .append("<input type=\"hidden\" name=\"checkOutDate\" value=\"").append(HtmlRenderer.escape(p.getCheckOutDate())).append("\">")
          .append("<button class=\"btn btn-pmc btn-lg\">Conferma check-out</button> ")
          .append("<a href=\"/checkout\" class=\"btn btn-outline-secondary btn-lg\">Annulla</a>")
          .append("</form>")
          .append("</div></div>");
        return sb.toString();
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
