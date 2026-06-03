package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.ManageTenantController;
import it.collegio.dto.CommittenteDettaglio;
import it.collegio.models.Indirizzo;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET  /tenants                    -> tabella + form add
 * GET  /tenants?edit=ID            -> form precompilato per update
 * POST /tenants                    -> action: add | update
 *
 * Solo ruolo AS, come da HomeView.configureButtonsForRole().
 * I dati arrivano da ManageTenantController.getDettagli() (DTO CommittenteDettaglio).
 * Le select gestori e indirizzi usano getAdminEmails() e getIndirizzi().
 */
public class ManageTenantHandler implements HttpHandler {

    private final ManageTenantController controller = new ManageTenantController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }
        if (!"AS".equalsIgnoreCase(SessionContext.roleType)) {
            HandlerUtils.redirect(ex, "/home");
            return;
        }

        String alert = "";
        Integer editId = null;
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            alert = handlePost(ex);
        } else {
            editId = parseEditQuery(ex.getRequestURI().getQuery());
        }

        List<CommittenteDettaglio> tenants = controller.getDettagli();
        CommittenteDettaglio editTenant = (editId != null) ? controller.getDettaglio(editId) : null;

        StringBuilder rows = new StringBuilder();
        if (tenants == null || tenants.isEmpty()) {
            rows.append("<tr><td colspan=\"7\" class=\"text-center text-muted\">Nessun committente</td></tr>");
        } else {
            for (CommittenteDettaglio c : tenants) {
                boolean highlight = editTenant != null && c.getCodCommittente() == editTenant.getCodCommittente();
                rows.append("<tr").append(highlight ? " class=\"table-warning\"" : "").append(">")
                    .append("<td><code>C").append(String.format("%04d", c.getCodCommittente())).append("</code></td>")
                    .append("<td>").append(HtmlRenderer.escape(c.getRagioneSociale())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(c.getEmail())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(c.getTelefono())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(c.getGestoreEmail())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(c.getVia())).append("</td>")
                    .append("<td class=\"text-end\">")
                    .append("<a href=\"/tenants?edit=").append(c.getCodCommittente())
                    .append("\" class=\"btn btn-sm btn-outline-pmc\">Modifica</a>")
                    .append("</td></tr>");
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(tenants != null ? tenants.size() : 0));
        values.put("alert", alert);
        values.put("formBlock", renderForm(editTenant));

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("manage_tenant.html", "Committenti - PM Collegio", values, layout));
    }

    private String handlePost(HttpExchange ex) throws IOException {
        Map<String, String> f = HandlerUtils.parseForm(ex);
        String action = f.getOrDefault("action", "");
        int codCommittente = parseInt(f.getOrDefault("codCommittente", "0"));
        String ragioneSociale = f.getOrDefault("ragioneSociale", "").trim();
        String gestoreEmail   = f.getOrDefault("gestoreEmail", "").trim();
        String email          = f.getOrDefault("email", "").trim();
        String telefono       = f.getOrDefault("telefono", "").trim();
        String via            = f.getOrDefault("via", "").trim();

        switch (action) {
            case "add":
                int newId = controller.getNextId();
                if (controller.addCommittente(newId, ragioneSociale, gestoreEmail, email, telefono, via)) {
                    return "<div class=\"alert alert-success\">Committente C" + String.format("%04d", newId)
                         + " (<strong>" + HtmlRenderer.escape(ragioneSociale) + "</strong>) creato</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile creare il committente</div>";
            case "update":
                if (controller.updateCommittente(codCommittente, ragioneSociale, gestoreEmail, email, telefono, via)) {
                    return "<div class=\"alert alert-success\">Committente C" + String.format("%04d", codCommittente) + " aggiornato</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile aggiornare il committente</div>";
            default:
                return "";
        }
    }

    private String renderForm(CommittenteDettaglio edit) {
        boolean isEdit = edit != null;
        int id          = isEdit ? edit.getCodCommittente() : controller.getNextId();
        String ragione  = isEdit ? edit.getRagioneSociale() : "";
        String gestore  = isEdit ? edit.getGestoreEmail()   : "";
        String email    = isEdit ? edit.getEmail()          : "";
        String telefono = isEdit ? edit.getTelefono()       : "";
        String via      = isEdit ? edit.getVia()            : "";

        List<String> admins = controller.getAdminEmails();
        List<Indirizzo> indirizzi = controller.getIndirizzi();

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card shadow-sm border-0 mb-4\"><div class=\"card-body p-4\">")
          .append("<h2 class=\"h5 mb-3\">").append(isEdit
                ? "Modifica committente C" + String.format("%04d", id)
                : "Nuovo committente").append("</h2>")
          .append("<form method=\"post\" action=\"/tenants\" class=\"row g-3\">")
          .append("<input type=\"hidden\" name=\"action\" value=\"").append(isEdit ? "update" : "add").append("\">");
        if (isEdit) {
            sb.append("<input type=\"hidden\" name=\"codCommittente\" value=\"").append(id).append("\">");
        }
        sb.append("<div class=\"col-md-3\"><label class=\"form-label\">Codice</label>")
          .append("<input type=\"text\" class=\"form-control\" value=\"C").append(String.format("%04d", id)).append("\" disabled></div>")
          .append("<div class=\"col-md-9\"><label class=\"form-label\">Ragione sociale *</label>")
          .append("<input type=\"text\" name=\"ragioneSociale\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(ragione)).append("\" required></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Gestore (admin)</label>")
          .append("<select name=\"gestoreEmail\" class=\"form-select\">")
          .append("<option value=\"\">-- nessuno --</option>");
        if (admins != null) {
            for (String a : admins) {
                sb.append("<option value=\"").append(HtmlRenderer.escape(a)).append("\"")
                  .append(a.equalsIgnoreCase(gestore) ? " selected" : "").append(">")
                  .append(HtmlRenderer.escape(a)).append("</option>");
            }
        }
        sb.append("</select></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Email *</label>")
          .append("<input type=\"email\" name=\"email\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(email)).append("\" required></div>")
          .append("<div class=\"col-md-4\"><label class=\"form-label\">Telefono</label>")
          .append("<input type=\"text\" name=\"telefono\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(telefono)).append("\"></div>")
          .append("<div class=\"col-md-8\"><label class=\"form-label\">Indirizzo (via)</label>")
          .append("<select name=\"via\" class=\"form-select\">")
          .append("<option value=\"\">-- usa indirizzo di default --</option>");
        if (indirizzi != null) {
            for (Indirizzo ind : indirizzi) {
                String v = ind.getVia() != null ? ind.getVia() : "";
                String label = v + (ind.getCitta() != null ? " (" + ind.getCitta() + ")" : "");
                sb.append("<option value=\"").append(HtmlRenderer.escape(v)).append("\"")
                  .append(v.equalsIgnoreCase(via) ? " selected" : "").append(">")
                  .append(HtmlRenderer.escape(label)).append("</option>");
            }
        }
        sb.append("</select></div>")
          .append("<div class=\"col-12 d-flex gap-2\">")
          .append("<button type=\"submit\" class=\"btn btn-pmc\">").append(isEdit ? "Salva modifiche" : "Crea committente").append("</button>");
        if (isEdit) {
            sb.append("<a href=\"/tenants\" class=\"btn btn-outline-secondary\">Annulla</a>");
        }
        sb.append("</div></form></div></div>");
        return sb.toString();
    }

    private Integer parseEditQuery(String query) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "edit".equals(kv[0])) {
                try { return Integer.valueOf(kv[1]); } catch (NumberFormatException e) { return null; }
            }
        }
        return null;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
