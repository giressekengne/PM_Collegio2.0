package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.ManageRoomController;
import it.collegio.enums.RoomStatus;
import it.collegio.models.Room;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET  /rooms          -> lista camere + form Add
 * POST /rooms          -> dispatch su action: add | update | delete
 *
 * Stessa identica logica della ManageRoomView Swing:
 *  - Add usa ManageRoomController.addRoom(roomId, tipo, prezzo, lettoTipo)
 *  - Update usa ManageRoomController.updateRoom(...)
 *  - Delete usa ManageRoomController.deleteRoom(id)  (vietato se OCCUPATA)
 *  - Il prossimo id si ottiene da controller.getNextId()
 */
public class RoomsHandler implements HttpHandler {

    private final ManageRoomController controller = new ManageRoomController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/login");
            return;
        }

        String rt = SessionContext.roleType != null ? SessionContext.roleType : "";
        boolean canManage = "AS".equalsIgnoreCase(rt) || "AC".equalsIgnoreCase(rt);
        if (!canManage) {
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

        List<Room> rooms = controller.getRooms();
        Room editRoom = (editId != null) ? controller.getById(editId) : null;

        StringBuilder rows = new StringBuilder();
        if (rooms == null || rooms.isEmpty()) {
            rows.append("<tr><td colspan=\"7\" class=\"text-center text-muted\">Nessuna camera trovata</td></tr>");
        } else {
            for (Room r : rooms) {
                boolean occupata = r.getStato() == RoomStatus.OCCUPATA;
                rows.append("<tr>")
                    .append("<td>R").append(String.format("%04d", r.getId())).append("</td>")
                    .append("<td>").append(r.getNumeroStanza()).append("</td>")
                    .append("<td>").append(safe(r.getrTipo())).append("</td>")
                    .append("<td>").append(safe(r.getLettoTipo())).append("</td>")
                    .append("<td>").append(String.format("%.2f", r.getPrezzo())).append(" &euro;</td>")
                    .append("<td>").append(statusBadge(r.getStato())).append("</td>")
                    .append("<td class=\"text-end\">");
                if (!occupata) {
                    rows.append("<a href=\"/rooms?edit=").append(r.getId())
                        .append("\" class=\"btn btn-sm btn-outline-pmc me-1\">Modifica</a>")
                        .append("<form method=\"post\" action=\"/rooms\" class=\"d-inline\" "
                              + "onsubmit=\"return confirm('Eliminare la camera R").append(String.format("%04d", r.getId())).append(" ?');\">")
                        .append("<input type=\"hidden\" name=\"action\" value=\"delete\">")
                        .append("<input type=\"hidden\" name=\"roomId\" value=\"").append(r.getId()).append("\">")
                        .append("<button class=\"btn btn-sm btn-outline-danger\">Elimina</button>")
                        .append("</form>");
                } else {
                    rows.append("<span class=\"text-muted small\">camera occupata</span>");
                }
                rows.append("</td></tr>");
            }
        }

        Map<String, String> values = new HashMap<>();
        values.put("rows", rows.toString());
        values.put("count", String.valueOf(rooms != null ? rooms.size() : 0));
        values.put("alert", alert);
        values.put("formBlock", renderForm(editRoom));

        Map<String, String> layout = new HashMap<>();
        layout.put("user", HtmlRenderer.escape((SessionContext.nome != null ? SessionContext.nome : "")
                + " " + (SessionContext.cognome != null ? SessionContext.cognome : "")));
        HandlerUtils.sendHtml(ex,
                HtmlRenderer.renderInLayout("rooms.html", "Camere - PM Collegio", values, layout));
    }

    private String handlePost(HttpExchange ex) throws IOException {
        Map<String, String> form = HandlerUtils.parseForm(ex);
        String action = form.getOrDefault("action", "");
        int roomId = parseInt(form.getOrDefault("roomId", "0"));
        String tipo = form.getOrDefault("tipo", "SINGOLA");
        String lettoTipo = form.getOrDefault("lettoTipo", "singolo");
        double prezzo = parseDouble(form.getOrDefault("prezzo", "0"));

        switch (action) {
            case "add":
                int newId = controller.getNextId();
                if (controller.addRoom(newId, tipo, prezzo, lettoTipo)) {
                    return "<div class=\"alert alert-success\">Camera R" + String.format("%04d", newId) + " creata</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile creare la camera (id gia' presente ?)</div>";
            case "update":
                if (controller.updateRoom(roomId, tipo, prezzo, lettoTipo)) {
                    return "<div class=\"alert alert-success\">Camera R" + String.format("%04d", roomId) + " aggiornata</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile aggiornare la camera (occupata ?)</div>";
            case "delete":
                if (controller.deleteRoom(roomId)) {
                    return "<div class=\"alert alert-warning\">Camera R" + String.format("%04d", roomId) + " eliminata</div>";
                }
                return "<div class=\"alert alert-danger\">Impossibile eliminare (occupata ?)</div>";
            default:
                return "";
        }
    }

    private String renderForm(Room edit) {
        boolean isEdit = edit != null;
        int id = isEdit ? edit.getId() : controller.getNextId();
        String tipo = isEdit && edit.getrTipo() != null ? edit.getrTipo().name() : "SINGOLA";
        String letto = isEdit && edit.getLettoTipo() != null ? edit.getLettoTipo().getDbValue() : "singolo";
        String prezzo = isEdit ? String.format("%.2f", edit.getPrezzo()) : "";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card shadow-sm border-0 mb-4\"><div class=\"card-body p-4\">")
          .append("<h2 class=\"h5 mb-3\">").append(isEdit ? "Modifica camera R" + String.format("%04d", id) : "Nuova camera").append("</h2>")
          .append("<form method=\"post\" action=\"/rooms\" class=\"row g-3\">")
          .append("<input type=\"hidden\" name=\"action\" value=\"").append(isEdit ? "update" : "add").append("\">");
        if (isEdit) {
            sb.append("<input type=\"hidden\" name=\"roomId\" value=\"").append(id).append("\">");
        }
        sb.append("<div class=\"col-md-3\"><label class=\"form-label\">ID</label>")
          .append("<input type=\"text\" class=\"form-control\" value=\"R").append(String.format("%04d", id)).append("\" disabled></div>")
          .append("<div class=\"col-md-3\"><label class=\"form-label\">Tipo</label>")
          .append("<select name=\"tipo\" class=\"form-select\">")
          .append(opt("SINGOLA", "Singola", tipo))
          .append(opt("DOPPIA", "Doppia", tipo))
          .append(opt("SUITE", "Suite", tipo))
          .append("</select></div>")
          .append("<div class=\"col-md-3\"><label class=\"form-label\">Tipo letto</label>")
          .append("<select name=\"lettoTipo\" class=\"form-select\">")
          .append(opt("singolo", "Singolo", letto))
          .append(opt("matrimoniale", "Matrimoniale", letto))
          .append(opt("king-size", "King-size", letto))
          .append("</select></div>")
          .append("<div class=\"col-md-3\"><label class=\"form-label\">Prezzo (€/notte)</label>")
          .append("<input type=\"number\" step=\"0.01\" min=\"0\" name=\"prezzo\" class=\"form-control\" value=\"").append(prezzo).append("\" required></div>")
          .append("<div class=\"col-12 d-flex gap-2\">")
          .append("<button type=\"submit\" class=\"btn btn-pmc\">").append(isEdit ? "Salva modifiche" : "Aggiungi camera").append("</button>");
        if (isEdit) {
            sb.append("<a href=\"/rooms\" class=\"btn btn-outline-secondary\">Annulla</a>");
        }
        sb.append("</div></form></div></div>");
        return sb.toString();
    }

    private String opt(String value, String label, String selected) {
        return "<option value=\"" + value + "\"" + (value.equalsIgnoreCase(selected) ? " selected" : "") + ">" + label + "</option>";
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

    private String safe(Object o) {
        return o == null ? "-" : HtmlRenderer.escape(o.toString());
    }

    private String statusBadge(RoomStatus s) {
        if (s == null) return "<span class=\"badge bg-secondary\">?</span>";
        switch (s) {
            case DISPONIBILE:  return "<span class=\"badge bg-success\">Disponibile</span>";
            case OCCUPATA:     return "<span class=\"badge bg-danger\">Occupata</span>";
            case MANUTENZIONE: return "<span class=\"badge bg-warning text-dark\">Manutenzione</span>";
            default:           return "<span class=\"badge bg-secondary\">" + s.name() + "</span>";
        }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
    private double parseDouble(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); } catch (NumberFormatException e) { return 0.0; }
    }
}
