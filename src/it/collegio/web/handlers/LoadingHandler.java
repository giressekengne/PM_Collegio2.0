package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.collegio.web.HtmlRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Replica della LoadingView Swing: welcome screen + tasto "Entra".
 * Non e' una pagina con dati: serve solo come introduzione all'app.
 */
public class LoadingHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("title", "Benvenuto - PM Collegio");
        HandlerUtils.sendHtml(ex, HtmlRenderer.render("loading.html", values));
    }
}
