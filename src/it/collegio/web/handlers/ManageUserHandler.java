package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.ManageUserController;
import it.collegio.enums.UserStatus;
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
 * GET  /users                      -> tabella + form add
 * GET  /users?edit=COUNTER         -> form precompilato per update
 * GET  /users?searchEmail=X        -> highlight + form precompilato sull'utente trovato
 * POST /users  action=add/update/delete
 *
 * Stessa logica di ManageUserView Swing: il filtro per ruolo U (vede solo se stesso)
 * e' gia' applicato dal Controller.
 */
public class ManageUserHandler implements HttpHandler {

    private final ManageUserController controller = new ManageUserController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String alert = "";
        String editCounter = null;
        String searchEmail = null;

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            alert = handlePost(ex);
        } else {
            Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
            editCounter = q.get("edit");
            searchEmail = q.get("searchEmail");
        }

        // Per ricerca: trova l'utente e precompila il form
        User editUser = null;
        if (searchEmail != null && !searchEmail.isEmpty()) {
            editUser = controller.cercaUserPerEmail(searchEmail);
            if (editUser == null) {
                alert = "<div class=\"alert alert-warning\">Nessun utente con email " + HtmlRenderer.escape(searchEmail) + "</div>";
            }
        } else if (editCounter != null) {
            // edit per counter: cerco l'utente nella lista (no metodo diretto getByCounter pubblico nel Controller)
            for (User u : controller.getUsersPerRuolo()) {
                if (editCounter.equals(u.getCounter())) {
                    editUser = u;
                    break;
                }
            }
        }

        List<User> users = controller.getUsersPerRuolo();
        boolean isUser = "U".equalsIgnoreCase(SessionContext.roleType);

        StringBuilder rows = new StringBuilder();
        if (users == null || users.isEmpty()) {
            rows.append("<tr><td colspan=\"8\" class=\"text-center text-muted\">Nessun utente</td></tr>");
        } else {
            for (User u : users) {
                boolean isHighlighted = editUser != null && u.getCounter().equals(editUser.getCounter());
                rows.append("<tr").append(isHighlighted ? " class=\"table-warning\"" : "").append(">")
                    .append("<td><code>").append(HtmlRenderer.escape(u.getCounter())).append("</code></td>")
                    .append("<td>").append(HtmlRenderer.escape(u.getNome())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(u.getCognome())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(u.getEmail())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(u.getMobile())).append("</td>")
                    .append("<td>").append(statoBadge(u.getStato())).append("</td>")
                    .append("<td class=\"text-muted small\">role #").append(u.getRole()).append("</td>")
                    .append("<td class=\"text-end\">");
                if (!isUser) {
                    rows.append("<a href=\"/users?edit=").append(urlEncode(u.getCounter()))
                        .append("\" class=\"btn btn-sm btn-outline-pmc me-1\">Modifica</a>")
                        .append("<form method=\"post\" action=\"/users\" class=\"d-inline\" "
                              + "onsubmit=\"return confirm('Eliminare l\\'utente ").append(HtmlRenderer.escape(u.getEmail())).append(" ?');\">")
                        .append("<input type=\"hidden\" name=\"action\" value=\"delete\">")
                        .append("<input type=\"hidden\" name=\"userCounter\" value=\"").append(HtmlRenderer.escape(u.getCounter())).append("\">")
                        .append("<button class=\"btn btn-sm btn-outline-danger\">Elimina</button>")
                        .append("</form>");
                } else {
                    rows.append("<span class=\"text-muted small\">read-only</span>");
                }
                rows.append("</td></tr>");
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(users != null ? users.size() : 0));
        values.put("alert", alert);
        values.put("searchValue", searchEmail != null ? HtmlRenderer.escape(searchEmail) : "");
        values.put("formBlock", isUser ? "" : renderForm(editUser));
        values.put("filterNote", isUser
                ? "Visualizzi solo il tuo account"
                : "Vista completa");

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("manage_user.html", "Utenti - PM Collegio", values, layout));
    }

    private String handlePost(HttpExchange ex) throws IOException {
        Map<String, String> f = HandlerUtils.parseForm(ex);
        String action = f.getOrDefault("action", "");
        String nome     = f.getOrDefault("nome", "").trim();
        String cognome  = f.getOrDefault("cognome", "").trim();
        String email    = f.getOrDefault("email", "").trim();
        String password = f.getOrDefault("password", "");
        String stato    = f.getOrDefault("stato", "ATTESA");
        String mobile   = f.getOrDefault("mobile", "").trim();
        String via      = f.getOrDefault("via", "").trim();
        String domanda  = f.getOrDefault("domanda", "").trim();
        String risposta = f.getOrDefault("risposta", "").trim();
        String genere   = f.getOrDefault("genere", "ALTRO");

        switch (action) {
            case "add":
                if (controller.insertUser(nome, cognome, email, password, stato, mobile, via, domanda, risposta, genere)) {
                    return "<div class=\"alert alert-success\">Utente <strong>" + HtmlRenderer.escape(email) + "</strong> creato</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile creare l'utente (email gia' in uso ?)</div>";
            case "update":
                if (controller.updateUser(email, nome, cognome, password, mobile, stato, domanda, risposta, genere)) {
                    return "<div class=\"alert alert-success\">Utente <strong>" + HtmlRenderer.escape(email) + "</strong> aggiornato</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile aggiornare (email non trovata ?)</div>";
            case "delete":
                String counter = f.getOrDefault("userCounter", "");
                if (controller.deleteUser(counter)) {
                    return "<div class=\"alert alert-warning\">Utente <code>" + HtmlRenderer.escape(counter) + "</code> eliminato</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile eliminare l'utente</div>";
            default:
                return "";
        }
    }

    private String renderForm(User edit) {
        boolean isEdit = edit != null;
        String nome    = isEdit ? edit.getNome()    : "";
        String cognome = isEdit ? edit.getCognome() : "";
        String email   = isEdit ? edit.getEmail()   : "";
        String mobile  = isEdit ? edit.getMobile()  : "";
        String dom     = isEdit ? edit.getRecupero(): "";
        String risp    = isEdit ? edit.getResponse(): "";
        String stato   = isEdit && edit.getStato() != null ? edit.getStato().name() : "ATTESA";
        String genere  = isEdit && edit.getGenere() != null ? edit.getGenere().name() : "ALTRO";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card shadow-sm border-0 mb-4\"><div class=\"card-body p-4\">")
          .append("<h2 class=\"h5 mb-3\">").append(isEdit ? "Modifica utente " + HtmlRenderer.escape(email) : "Nuovo utente").append("</h2>")
          .append("<form method=\"post\" action=\"/users\" class=\"row g-3\">")
          .append("<input type=\"hidden\" name=\"action\" value=\"").append(isEdit ? "update" : "add").append("\">")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Nome *</label>")
          .append("<input type=\"text\" name=\"nome\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(nome)).append("\" required></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Cognome</label>")
          .append("<input type=\"text\" name=\"cognome\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(cognome)).append("\"></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Email *</label>")
          .append("<input type=\"email\" name=\"email\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(email)).append("\" ")
          .append(isEdit ? "readonly" : "required").append("></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">").append(isEdit ? "Nuova password" : "Password *").append("</label>")
          .append("<input type=\"password\" name=\"password\" class=\"form-control\" ").append(isEdit ? "" : "required").append("></div>")
          .append("<div class=\"col-md-4\"><label class=\"form-label\">Telefono</label>")
          .append("<input type=\"text\" name=\"mobile\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(mobile)).append("\"></div>")
          .append("<div class=\"col-md-4\"><label class=\"form-label\">Stato</label>")
          .append("<select name=\"stato\" class=\"form-select\">")
          .append(opt("ATTIVO", "Attivo", stato))
          .append(opt("ATTESA", "In attesa", stato))
          .append(opt("DISATTIVATO", "Disattivato", stato))
          .append("</select></div>")
          .append("<div class=\"col-md-4\"><label class=\"form-label\">Genere</label>")
          .append("<select name=\"genere\" class=\"form-select\">")
          .append(opt("MASCHIO", "Maschio", genere))
          .append(opt("FEMMINA", "Femmina", genere))
          .append(opt("ALTRO", "Altro", genere))
          .append("</select></div>")
          .append("<div class=\"col-md-12\"><label class=\"form-label\">Indirizzo (via)</label>")
          .append("<input type=\"text\" name=\"via\" class=\"form-control\" placeholder=\"se vuoto, indirizzo di default\"></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Domanda di sicurezza</label>")
          .append("<input type=\"text\" name=\"domanda\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(dom)).append("\"></div>")
          .append("<div class=\"col-md-6\"><label class=\"form-label\">Risposta</label>")
          .append("<input type=\"text\" name=\"risposta\" class=\"form-control\" value=\"").append(HtmlRenderer.escape(risp)).append("\"></div>")
          .append("<div class=\"col-12 d-flex gap-2\">")
          .append("<button type=\"submit\" class=\"btn btn-pmc\">").append(isEdit ? "Salva modifiche" : "Crea utente").append("</button>");
        if (isEdit) {
            sb.append("<a href=\"/users\" class=\"btn btn-outline-secondary\">Annulla</a>");
        }
        sb.append("</div></form></div></div>");
        return sb.toString();
    }

    private String opt(String value, String label, String selected) {
        return "<option value=\"" + value + "\"" + (value.equalsIgnoreCase(selected) ? " selected" : "") + ">" + label + "</option>";
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

    private String statoBadge(UserStatus stato) {
        if (stato == null) return "<span class=\"badge bg-secondary\">?</span>";
        switch (stato) {
            case ATTIVO:      return "<span class=\"badge bg-success\">Attivo</span>";
            case ATTESA:      return "<span class=\"badge bg-warning text-dark\">In attesa</span>";
            case DISATTIVATO: return "<span class=\"badge bg-danger\">Disattivato</span>";
            default:          return "<span class=\"badge bg-secondary\">" + stato.name() + "</span>";
        }
    }
}
