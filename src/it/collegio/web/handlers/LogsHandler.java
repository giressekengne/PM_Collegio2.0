package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.dao.AccessLogDao;
import it.collegio.dto.AccessLogDettaglio;
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
 * Pagina di audit accessi. Visibile solo a AS e AC, come da HomeView.configureButtonsForRole().
 * I dati arrivano dallo stesso DAO della LogsView Swing.
 */
public class LogsHandler implements HttpHandler {

    private final AccessLogDao logDao = new AccessLogDao();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }
        String rt = SessionContext.roleType != null ? SessionContext.roleType : "";
        if (!"AS".equalsIgnoreCase(rt) && !"AC".equalsIgnoreCase(rt)) {
            HandlerUtils.redirect(ex, "/home");
            return;
        }

        List<AccessLogDettaglio> logs = logDao.getDettagli();
        StringBuilder rows = new StringBuilder();
        if (logs == null || logs.isEmpty()) {
            rows.append("<tr><td colspan=\"5\" class=\"text-center text-muted\">Nessun accesso registrato</td></tr>");
        } else {
            for (AccessLogDettaglio l : logs) {
                String logout = l.getLogoutTime() != null
                        ? l.getLogoutTime().format(FMT)
                        : "<span class=\"badge bg-info\">sessione aperta</span>";
                rows.append("<tr>")
                    .append("<td>").append(HtmlRenderer.escape(l.getUserNome())).append("</td>")
                    .append("<td>").append(HtmlRenderer.escape(l.getRoleNome())).append("</td>")
                    .append("<td><code>").append(HtmlRenderer.escape(l.getIpAddress())).append("</code></td>")
                    .append("<td>").append(l.getLoginTime() != null ? l.getLoginTime().format(FMT) : "-").append("</td>")
                    .append("<td>").append(logout).append("</td>")
                    .append("</tr>");
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(logs != null ? logs.size() : 0));

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("logs.html", "Access logs - PM Collegio", values, layout));
    }

    private static String fmt(LocalDateTime d) {
        return d == null ? "-" : d.format(FMT);
    }
}
