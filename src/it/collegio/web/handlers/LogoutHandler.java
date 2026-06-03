package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.controllers.LoginController;
import it.collegio.web.WebSession;

import java.io.IOException;

/**
 * Riusa LoginController.registraLogout() esattamente come la HomeView Swing.
 */
public class LogoutHandler implements HttpHandler {

    private final LoginController loginController = new LoginController();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        loginController.registraLogout();
        WebSession.clearCookie(ex);
        HandlerUtils.redirect(ex, "/login");
    }
}
