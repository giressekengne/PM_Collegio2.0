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
 * GET  /gestione-fatture        -> lista fatture filtrata per ruolo dal Controller
 * POST /gestione-fatture/paga   -> FatturaController.paga(id, metodoId)
 * POST /gestione-fatture/annulla-> FatturaController.annulla(id)
 */
public class GestioneFattureHandler implements HttpHandler {

    private final FatturaController controller = new FatturaController();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String alert = "";
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> form = HandlerUtils.parseForm(ex);
            String action = form.getOrDefault("action", "");
            int fatturaId = parseInt(form.getOrDefault("fatturaId", "0"));
            if ("paga".equals(action)) {
                int metodoId = parseInt(form.getOrDefault("metodoId", "0"));
                alert = controller.paga(fatturaId, metodoId)
                        ? "<div class=\"alert alert-success\">Pagamento registrato per F"
                          + String.format("%04d", fatturaId) + "</div>"
                        : "<div class=\"alert alert-danger\">Pagamento fallito per F"
                          + String.format("%04d", fatturaId) + "</div>";
            } else if ("annulla".equals(action)) {
                alert = controller.annulla(fatturaId)
                        ? "<div class=\"alert alert-warning\">F" + String.format("%04d", fatturaId)
                          + " contrassegnata come NON PAGATA</div>"
                        : "<div class=\"alert alert-danger\">Operazione fallita</div>";
            }
        }

        List<FatturaDettaglio> list = controller.getDettagliPerRuolo();
        List<MetodoPagamento> metodi = controller.getMetodiPagamento();

        StringBuilder metodiOpts = new StringBuilder();
        if (metodi != null) {
            for (MetodoPagamento m : metodi) {
                metodiOpts.append("<option value=\"").append(m.getId()).append("\">")
                          .append(HtmlRenderer.escape(m.getNome())).append("</option>");
            }
        }

        StringBuilder rows = new StringBuilder();
        if (list == null || list.isEmpty()) {
            rows.append("<tr><td colspan=\"7\" class=\"text-center text-muted\">Nessuna fattura</td></tr>");
        } else {
            for (FatturaDettaglio f : list) {
                boolean pagabile = controller.isPagabile(f.getStato());
                rows.append("<tr>")
                    .append("<td><code>F").append(String.format("%04d", f.getFatturaId())).append("</code></td>")
                    .append("<td>").append(HtmlRenderer.escape(f.getClienteNome())).append("</td>")
                    .append("<td>R").append(String.format("%04d", f.getNumeroStanza())).append("</td>")
                    .append("<td>").append(String.format("%.2f", f.getImporto())).append(" €</td>")
                    .append("<td>").append(formatDate(f.getDataEmissione())).append("</td>")
                    .append("<td>").append(stateBadge(f.getStato())).append("</td>")
                    .append("<td class=\"text-end\">")
                    .append("<a href=\"/fattura?id=").append(f.getFatturaId())
                    .append("\" class=\"btn btn-sm btn-outline-secondary me-1\">Vedi</a>");
                if (pagabile) {
                    rows.append("<button class=\"btn btn-sm btn-pmc me-1\" data-bs-toggle=\"modal\" "
                              + "data-bs-target=\"#pay-").append(f.getFatturaId()).append("\">Paga</button>")
                        .append("<form method=\"post\" action=\"/gestione-fatture\" class=\"d-inline\">")
                        .append("<input type=\"hidden\" name=\"action\" value=\"annulla\">")
                        .append("<input type=\"hidden\" name=\"fatturaId\" value=\"").append(f.getFatturaId()).append("\">")
                        .append("<button class=\"btn btn-sm btn-outline-danger\">Annulla</button>")
                        .append("</form>");
                }
                rows.append("</td></tr>");

                if (pagabile) {
                    rows.append(payModal(f.getFatturaId(), f.getImporto(), metodiOpts.toString()));
                }
            }
        }

        boolean isUser = "U".equalsIgnoreCase(SessionContext.roleType);
        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(list != null ? list.size() : 0));
        values.put("alert", alert);
        values.put("filterNote", isUser
                ? "Vedi solo le tue fatture"
                : "Vista completa");

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("gestione_fatture.html", "Fatture - PM Collegio", values, layout));
    }

    private String payModal(int id, double importo, String metodiOpts) {
        return "<tr class=\"d-none\"><td>"
             + "<div class=\"modal fade\" id=\"pay-" + id + "\" tabindex=\"-1\">"
             + "<div class=\"modal-dialog\"><div class=\"modal-content\">"
             + "<form method=\"post\" action=\"/gestione-fatture\">"
             + "<input type=\"hidden\" name=\"action\" value=\"paga\">"
             + "<input type=\"hidden\" name=\"fatturaId\" value=\"" + id + "\">"
             + "<div class=\"modal-header\"><h5 class=\"modal-title\">Pagamento F" + String.format("%04d", id) + "</h5>"
             + "<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\"></button></div>"
             + "<div class=\"modal-body\">"
             + "<p>Importo: <strong>" + String.format("%.2f", importo) + " €</strong></p>"
             + "<label class=\"form-label\">Metodo</label>"
             + "<select name=\"metodoId\" class=\"form-select\" required>" + metodiOpts + "</select>"
             + "</div>"
             + "<div class=\"modal-footer\">"
             + "<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Annulla</button>"
             + "<button type=\"submit\" class=\"btn btn-pmc\">Conferma pagamento</button>"
             + "</div></form></div></div></div>"
             + "</td></tr>";
    }

    private String stateBadge(FatturaStatus s) {
        if (s == null) return "<span class=\"badge bg-secondary\">?</span>";
        switch (s) {
            case PAGATA:     return "<span class=\"badge bg-success\">Pagata</span>";
            case IN_ATTESA:  return "<span class=\"badge bg-warning text-dark\">In attesa</span>";
            case NON_PAGATA: return "<span class=\"badge bg-danger\">Non pagata</span>";
            default:         return "<span class=\"badge bg-secondary\">" + s.name() + "</span>";
        }
    }

    private String formatDate(LocalDateTime d) {
        return d == null ? "-" : d.format(FMT);
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
