package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.web.HtmlRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Equivalente della Loading1View Swing: animazione di transizione prima del login.
 * La progress bar in HTML e' una pura animazione CSS, l'auto-redirect avviene via meta refresh.
 */
public class Loading1Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("title", "Caricamento - PM Collegio");
        HandlerUtils.sendHtml(ex, HtmlRenderer.render("loading1.html", values));
    }
}
