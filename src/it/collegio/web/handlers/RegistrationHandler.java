package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.RegistrationController;
import it.collegio.dto.LoginResponse;
import it.collegio.models.Indirizzo;
import it.collegio.web.HtmlRenderer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Riusa RegistrationController.registra(): stessa firma, stessa logica.
 * In v1 questa stessa funzione richiedeva di duplicare validazioni + INSERT
 * in una nuova classe per ogni front-end.
 */
public class RegistrationHandler implements HttpHandler {

    private final RegistrationController controller = new RegistrationController();

    // Stessa lista hardcoded nella RegistrationView Swing (questionComboBox)
    private static final List<String> DOMANDE = Arrays.asList(
            "giocatore preferito?",
            "disciplina preferita?",
            "cantante preferito?",
            "libro preferita?",
            "programma televisivo preferito?"
    );

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> form = HandlerUtils.parseForm(ex);
            LoginResponse resp = controller.registra(
                    form.getOrDefault("nome", "").trim(),
                    form.getOrDefault("cognome", "").trim(),
                    form.getOrDefault("email", "").trim(),
                    form.getOrDefault("password", ""),
                    form.getOrDefault("telefono", "").trim(),
                    form.getOrDefault("via", "").trim(),
                    form.getOrDefault("domanda", "").trim(),
                    form.getOrDefault("risposta", "").trim(),
                    form.getOrDefault("genere", "ALTRO")
            );

            if (resp.isSuccessful()) {
                showForm(ex,
                        "<div class=\"alert alert-success\">Registrazione inviata. L'account e' in stato ATTESA, "
                        + "attendi l'approvazione di un amministratore.</div>",
                        new HashMap<String, String>());
                return;
            }
            showForm(ex, "<div class=\"alert alert-danger\">"
                    + HtmlRenderer.escape(resp.getErrorMessage()) + "</div>", form);
            return;
        }
        showForm(ex, "", new HashMap<String, String>());
    }

    private void showForm(HttpExchange ex, String alert, Map<String, String> prev) throws IOException {
        String prevVia = prev.getOrDefault("via", "");
        String prevDomanda = prev.getOrDefault("domanda", "");

        StringBuilder viaOptions = new StringBuilder();
        viaOptions.append("<option value=\"\">-- usa indirizzo di default --</option>");
        List<Indirizzo> indirizzi = controller.getIndirizzi();
        if (indirizzi != null) {
            for (Indirizzo i : indirizzi) {
                String v = i.getVia() != null ? i.getVia() : "";
                String label = v + (i.getCitta() != null ? " (" + i.getCitta() + ")" : "");
                viaOptions.append("<option value=\"").append(HtmlRenderer.escape(v)).append("\"")
                          .append(v.equalsIgnoreCase(prevVia) ? " selected" : "").append(">")
                          .append(HtmlRenderer.escape(label)).append("</option>");
            }
        }

        StringBuilder domandaOptions = new StringBuilder();
        domandaOptions.append("<option value=\"\">-- seleziona --</option>");
        for (String d : DOMANDE) {
            domandaOptions.append("<option value=\"").append(HtmlRenderer.escape(d)).append("\"")
                          .append(d.equalsIgnoreCase(prevDomanda) ? " selected" : "").append(">")
                          .append(HtmlRenderer.escape(d)).append("</option>");
        }

        Map<String, String> values = new HashMap<>();
        values.put("title", "Registrazione - PM Collegio");
        values.put("alert", alert);
        values.put("nome",     HtmlRenderer.escape(prev.getOrDefault("nome", "")));
        values.put("cognome",  HtmlRenderer.escape(prev.getOrDefault("cognome", "")));
        values.put("email",    HtmlRenderer.escape(prev.getOrDefault("email", "")));
        values.put("telefono", HtmlRenderer.escape(prev.getOrDefault("telefono", "")));
        values.put("viaOptions",     viaOptions.toString());
        values.put("domandaOptions", domandaOptions.toString());
        HandlerUtils.sendHtml(ex, HtmlRenderer.render("registration.html", values));
    }
}
