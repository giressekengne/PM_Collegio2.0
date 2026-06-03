package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.LoginController;
import it.collegio.dto.LoginResponse;
import it.collegio.utilities.SessionContext;
import it.collegio.web.HtmlRenderer;
import it.collegio.web.WebSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GET  /login -> mostra il form
 * POST /login -> chiama LoginController.login(email, password) e popola SessionContext.
 *
 * Stessa identica chiamata che fa la LoginView Swing. Zero logica duplicata.
 */
public class LoginHandler implements HttpHandler {

    private final LoginController loginController = new LoginController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            handlePost(ex);
        } else {
            handleGet(ex, null);
        }
    }

    private void handleGet(HttpExchange ex, String errorMessage) throws IOException {
        if (WebSession.isLoggedIn()) {
            HandlerUtils.redirect(ex, "/home");
            return;
        }
        Map<String, String> values = new HashMap<>();
        values.put("title", "Login - PM Collegio");
        values.put("error", errorMessage != null
                ? "<div class=\"alert alert-danger\">" + HtmlRenderer.escape(errorMessage) + "</div>"
                : "");

        // Il login e' una pagina full-screen autonoma: niente layout/navbar.
        String html = HtmlRenderer.render("login.html", values);
        HandlerUtils.sendHtml(ex, html);
    }

    private void handlePost(HttpExchange ex) throws IOException {
        Map<String, String> form = HandlerUtils.parseForm(ex);
        String email = form.getOrDefault("email", "").trim();
        String password = form.getOrDefault("password", "");

        if (email.isEmpty() || password.isEmpty()) {
            handleGet(ex, "Inserire email e password");
            return;
        }

        LoginResponse resp = loginController.login(email, password);
        if (!resp.isSuccessful()) {
            handleGet(ex, resp.getErrorMessage());
            return;
        }

        WebSession.issueCookie(ex);
        HandlerUtils.redirect(ex, "/home");
        // SessionContext e' gia' stato popolato dentro LoginController.login()
        // come fa la LoginView desktop. Nessuna logica replicata.
        if (SessionContext.userCounter == null) {
            // safety net, non dovrebbe mai accadere
        }
    }
}
