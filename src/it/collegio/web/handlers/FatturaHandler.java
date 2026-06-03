package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.FatturaController;
import it.collegio.dto.FatturaDettaglio;
import it.collegio.enums.FatturaStatus;
import it.collegio.models.MetodoPagamento;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET  /fattura?id=N           -> dettaglio fattura
 * POST /fattura action=paga    -> FatturaController.paga(fatturaId, metodoId)
 * POST /fattura action=annulla -> FatturaController.annulla(fatturaId)
 *
 * Allinea l'HTML alla FatturaView Swing: la fattura "in attesa" e' azionabile
 * direttamente dal dettaglio, senza dover passare dalla lista.
 */
public class FatturaHandler implements HttpHandler {

    private final FatturaController controller = new FatturaController();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String alert = "";
        int id;
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> f = HandlerUtils.parseForm(ex);
            id = parseInt(f.getOrDefault("fatturaId", "0"));
            String action = f.getOrDefault("action", "");
            if ("paga".equals(action)) {
                int metodoId = parseInt(f.getOrDefault("metodoId", "0"));
                alert = controller.paga(id, metodoId)
                        ? "<div class=\"alert alert-success\">Pagamento registrato per F" + String.format("%04d", id) + "</div>"
                        : "<div class=\"alert alert-danger\">Pagamento fallito per F" + String.format("%04d", id) + "</div>";
            } else if ("annulla".equals(action)) {
                alert = controller.annulla(id)
                        ? "<div class=\"alert alert-warning\">F" + String.format("%04d", id) + " contrassegnata come NON PAGATA</div>"
                        : "<div class=\"alert alert-danger\">Operazione fallita</div>";
            }
        } else {
            id = parseId(ex.getRequestURI().getQuery());
        }

        FatturaDettaglio f = controller.getDettaglio(id);
        String body;
        if (f == null) {
            body = "<div class=\"alert alert-warning\">Fattura " + id + " non trovata</div>";
        } else {
            body = renderInvoice(f, controller.isPagabile(f.getStato()));
        }

        Map<String, String> values = new HashMap<>();
        values.put("body", alert + body);

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("fattura.html", "Fattura - PM Collegio", values, layout));
    }

    private String renderInvoice(FatturaDettaglio f, boolean pagabile) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"invoice card shadow-sm border-0\"><div class=\"card-body p-5\">")
          .append("<div class=\"d-flex justify-content-between align-items-start mb-4\">")
          .append("  <div><div class=\"brand-logo mb-3\"></div>")
          .append("    <h1 class=\"h3 mb-1\">PM Collegio</h1>")
          .append("    <p class=\"text-muted small mb-0\">Universita di Pavia - Servizio collegi</p></div>")
          .append("  <div class=\"text-end\">")
          .append("    <div class=\"text-uppercase text-muted small\">Fattura</div>")
          .append("    <div class=\"h2 mb-2\">F").append(String.format("%04d", f.getFatturaId())).append("</div>")
          .append("    ").append(stateBadge(f.getStato()))
          .append("  </div>")
          .append("</div>")
          .append("<hr>")
          .append("<dl class=\"row mt-3\">")
          .append("<dt class=\"col-sm-4\">Cliente</dt><dd class=\"col-sm-8\">").append(HtmlRenderer.escape(f.getClienteNome())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Camera</dt><dd class=\"col-sm-8\">R").append(String.format("%04d", f.getNumeroStanza())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Prenotazione</dt><dd class=\"col-sm-8\">P").append(String.format("%04d", f.getReservationId())).append("</dd>")
          .append("<dt class=\"col-sm-4\">Data emissione</dt><dd class=\"col-sm-8\">").append(formatDate(f.getDataEmissione())).append("</dd>")
          .append("</dl>")
          .append("<div class=\"d-flex justify-content-between align-items-center mt-4 p-3 bg-pmc-soft rounded\">")
          .append("  <div class=\"text-pmc fw-bold\">Totale dovuto</div>")
          .append("  <div class=\"display-6 text-pmc fw-bold\">").append(String.format("%.2f", f.getImporto())).append(" €</div>")
          .append("</div>")
          .append("<div class=\"d-flex gap-2 mt-4 flex-wrap\">")
          .append("  <a href=\"/gestione-fatture\" class=\"btn btn-outline-secondary\">&larr; Lista fatture</a>")
          .append("  <button class=\"btn btn-outline-pmc\" onclick=\"window.print()\">Stampa</button>");

        if (pagabile) {
            sb.append("  <button class=\"btn btn-pmc\" data-bs-toggle=\"modal\" data-bs-target=\"#pay-modal\">Paga</button>")
              .append("  <form method=\"post\" action=\"/fattura?id=").append(f.getFatturaId()).append("\" "
                       + "onsubmit=\"return confirm('Contrassegnare come NON PAGATA ?');\">")
              .append("    <input type=\"hidden\" name=\"action\" value=\"annulla\">")
              .append("    <input type=\"hidden\" name=\"fatturaId\" value=\"").append(f.getFatturaId()).append("\">")
              .append("    <button class=\"btn btn-outline-danger\">Annulla</button>")
              .append("  </form>");
        }

        sb.append("</div></div></div>");

        if (pagabile) {
            sb.append(renderPayModal(f));
        }
        return sb.toString();
    }

    private String renderPayModal(FatturaDettaglio f) {
        List<MetodoPagamento> metodi = controller.getMetodiPagamento();
        StringBuilder opts = new StringBuilder();
        if (metodi != null) {
            for (MetodoPagamento m : metodi) {
                opts.append("<option value=\"").append(m.getId()).append("\">")
                    .append(HtmlRenderer.escape(m.getNome())).append("</option>");
            }
        }
        return "<div class=\"modal fade\" id=\"pay-modal\" tabindex=\"-1\">"
             + "<div class=\"modal-dialog\"><div class=\"modal-content\">"
             + "<form method=\"post\" action=\"/fattura?id=" + f.getFatturaId() + "\">"
             + "<input type=\"hidden\" name=\"action\" value=\"paga\">"
             + "<input type=\"hidden\" name=\"fatturaId\" value=\"" + f.getFatturaId() + "\">"
             + "<div class=\"modal-header\"><h5 class=\"modal-title\">Pagamento F"
             + String.format("%04d", f.getFatturaId()) + "</h5>"
             + "<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\"></button></div>"
             + "<div class=\"modal-body\">"
             + "<p>Importo: <strong>" + String.format("%.2f", f.getImporto()) + " €</strong></p>"
             + "<label class=\"form-label\">Metodo di pagamento</label>"
             + "<select name=\"metodoId\" class=\"form-select\" required>" + opts + "</select>"
             + "</div>"
             + "<div class=\"modal-footer\">"
             + "<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Annulla</button>"
             + "<button type=\"submit\" class=\"btn btn-pmc\">Conferma pagamento</button>"
             + "</div></form></div></div></div>"
             + "<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\"></script>";
    }

    private String stateBadge(Object stato) {
        if (stato == null) return "<span class=\"badge bg-secondary\">?</span>";
        String s = stato.toString();
        switch (s) {
            case "PAGATA":     return "<span class=\"badge bg-success fs-6\">Pagata</span>";
            case "IN_ATTESA":  return "<span class=\"badge bg-warning text-dark fs-6\">In attesa</span>";
            case "NON_PAGATA": return "<span class=\"badge bg-danger fs-6\">Non pagata</span>";
            default:           return "<span class=\"badge bg-secondary fs-6\">" + HtmlRenderer.escape(s) + "</span>";
        }
    }

    private String formatDate(LocalDateTime d) {
        return d == null ? "-" : d.format(FMT);
    }

    private int parseId(String query) {
        if (query == null) return 0;
        for (String p : query.split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && "id".equals(kv[0])) {
                try { return Integer.parseInt(kv[1]); } catch (NumberFormatException e) { return 0; }
            }
        }
        return 0;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    // Suppress unused warning
    @SuppressWarnings("unused")
    private FatturaStatus _unused;
}
