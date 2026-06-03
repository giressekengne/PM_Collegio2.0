package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.PasswordResetController;
import it.collegio.web.HtmlRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Flusso a due step come la PassWordView Swing:
 *   1) POST con solo email -> chiede a PasswordResetController.getDomandaSicurezza()
 *      e mostra la domanda registrata dall'utente.
 *   2) POST con email + risposta + nuova password -> PasswordResetController.resetPassword().
 */
public class PasswordResetHandler implements HttpHandler {

    private final PasswordResetController controller = new PasswordResetController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> form = HandlerUtils.parseForm(ex);
            String email = form.getOrDefault("email", "").trim();
            String risposta = form.getOrDefault("risposta", "").trim();
            String nuovaPassword = form.getOrDefault("password", "");
            String domandaPrec = form.getOrDefault("domanda", "").trim();

            if (email.isEmpty()) {
                renderStep1(ex, email, "<div class=\"alert alert-danger\">Inserire un'email</div>");
                return;
            }

            if (risposta.isEmpty() || nuovaPassword.isEmpty()) {
                // Step 1: cerca la domanda di sicurezza
                String domanda = controller.getDomandaSicurezza(email);
                if (domanda == null) {
                    renderStep1(ex, email, "<div class=\"alert alert-danger\">Email non registrata</div>");
                    return;
                }
                renderStep2(ex, email, domanda, "");
                return;
            }

            // Step 2: reset effettivo
            boolean ok = controller.resetPassword(email, risposta, nuovaPassword);
            if (ok) {
                renderStep1(ex, "",
                        "<div class=\"alert alert-success\">Password aggiornata. Procedi al login.</div>");
            } else {
                renderStep2(ex, email, domandaPrec,
                        "<div class=\"alert alert-danger\">Risposta errata o impossibile aggiornare la password</div>");
            }
            return;
        }
        renderStep1(ex, "", "");
    }

    private void renderStep1(HttpExchange ex, String email, String alert) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("title", "Recupero password - PM Collegio");
        values.put("alert", alert);
        values.put("email", HtmlRenderer.escape(email));
        HandlerUtils.sendHtml(ex, HtmlRenderer.render("password_step1.html", values));
    }

    private void renderStep2(HttpExchange ex, String email, String domanda, String alert) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("title", "Recupero password - PM Collegio");
        values.put("alert", alert);
        values.put("email", HtmlRenderer.escape(email));
        values.put("domanda", HtmlRenderer.escape(domanda));
        HandlerUtils.sendHtml(ex, HtmlRenderer.render("password_step2.html", values));
    }
}
